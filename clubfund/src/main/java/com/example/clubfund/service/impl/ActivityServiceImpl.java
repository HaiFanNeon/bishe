package com.example.clubfund.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.clubfund.common.Result;
import com.example.clubfund.dto.ActivityQueryDTO;
import com.example.clubfund.dto.ActivitySubmitDTO;
import com.example.clubfund.dto.AuditDTO;
import com.example.clubfund.entity.ActivityApply;
import com.example.clubfund.entity.AuditLog;
import com.example.clubfund.entity.SysUser;
import com.example.clubfund.mapper.ActivityApplyMapper;
import com.example.clubfund.mapper.AuditLogMapper;
import com.example.clubfund.mapper.SysUserMapper;
import com.example.clubfund.service.IActivityService;
import com.example.clubfund.service.IMessageService;
import com.example.clubfund.utils.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityApplyMapper, ActivityApply> implements IActivityService {

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private SysUserMapper userMapper; // 注入用户Mapper以获取详情

    @Autowired
    private IMessageService messageService; // 注入消息服务

    @Override
    public Result selectApplyPage(ActivityQueryDTO queryDTO) {
        Page<ActivityApply> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        QueryWrapper<ActivityApply> queryWrapper = new QueryWrapper<>();

        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            queryWrapper.and(w -> w.like("title", queryDTO.getKeyword())
                    .or().like("club_name", queryDTO.getKeyword()));
        }
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq("status", queryDTO.getStatus());
        }
        // 如果是普通用户，只能查自己的；如果是管理员(userId为空)，查所有
        if (queryDTO.getUserId() != null) {
            queryWrapper.eq("user_id", queryDTO.getUserId());
        }

        queryWrapper.orderByDesc("create_time");
        return Result.success(baseMapper.selectPage(page, queryWrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务
    public Result auditApply(AuditDTO auditDTO) {
        ActivityApply apply = baseMapper.selectById(auditDTO.getApplyId());
        if (apply == null) {
            return Result.error("申请记录不存在");
        }

        // 1. 更新申请表主状态
        apply.setStatus(auditDTO.getAction());
        apply.setCurrentAuditComment(auditDTO.getComment());
        baseMapper.updateById(apply);

        // 2. 插入审核历史记录
        AuditLog log = new AuditLog();
        log.setApplyId(apply.getId());
        log.setOperatorName(auditDTO.getOperatorName());
        log.setOperateAction(auditDTO.getAction());
        log.setComment(auditDTO.getComment());
        log.setCreateTime(LocalDateTime.now());
        auditLogMapper.insert(log);


        // --- 新增：发送通知 ---
        String statusText = "";
        if (auditDTO.getAction() == 1) statusText = "已批准";
        else if (auditDTO.getAction() == 2) statusText = "已被驳回";
        else if (auditDTO.getAction() == 3) statusText = "需补充材料";

        String title = "活动审批通知";
        String content = String.format("您申请的活动【%s】审核状态更新为：%s。审核意见：%s",
                apply.getTitle(), statusText, auditDTO.getComment());

        // 发送给申请人
        messageService.sendSysMessage(apply.getUserId(), title, content);

        return Result.success("审核完成");
    }

    @Override
    public Result getDetailWithHistory(Long id) {
        // 查详情
        ActivityApply apply = baseMapper.selectById(id);
        if (apply == null) return Result.error("记录不存在");

        // 查历史日志
        QueryWrapper<AuditLog> logWrapper = new QueryWrapper<>();
        logWrapper.eq("apply_id", id).orderByDesc("create_time");
        List<AuditLog> logs = auditLogMapper.selectList(logWrapper);

        Map<String, Object> map = new HashMap<>();
        map.put("apply", apply);
        map.put("logs", logs);

        return Result.success(map);
    }

    @Override
    public Result submitApply(ActivityApply apply) {
        apply.setStatus(0); // 默认为待审核
        apply.setCreateTime(LocalDateTime.now());
        save(apply);
        return Result.success("申请提交成功");
    }

    @Override
    public Result createActivity(ActivitySubmitDTO submitDTO) {
        // 1. 获取当前登录用户ID
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }

        // 2. 获取用户详细信息 (真实姓名、社团)
        // 这里为了保证数据准确，必须查库，不能信前端
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户信息异常");
        }
        if (StrUtil.isBlank(user.getClubName())) {
            return Result.error("您尚未加入任何社团，无法申请活动");
        }

        // 3. 组装实体对象
        ActivityApply apply = new ActivityApply();
        // 复制业务字段 (title, content, budgetAmount)
        BeanUtils.copyProperties(submitDTO, apply);

        // 填充身份字段
        apply.setUserId(user.getId());
        apply.setRealName(user.getRealName());
        apply.setClubName(user.getClubName());

        // 初始化状态
        apply.setStatus(0); // 待审核
        apply.setCreateTime(LocalDateTime.now());
        apply.setDeleted(0);

        // 4. 保存
        save(apply);

        return Result.success("活动申请已提交，请等待管理员审核");
    }
}