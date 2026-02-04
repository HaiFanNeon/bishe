package com.example.clubfund.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.clubfund.common.Result;
import com.example.clubfund.dto.ReimburseAuditDTO;
import com.example.clubfund.dto.ReimburseSubmitDTO;
import com.example.clubfund.entity.ActivityApply;
import com.example.clubfund.entity.Reimbursement;
import com.example.clubfund.entity.SysMessage;
import com.example.clubfund.entity.SysUser;
import com.example.clubfund.mapper.ActivityApplyMapper;
import com.example.clubfund.mapper.ReimbursementMapper;
import com.example.clubfund.mapper.SysUserMapper;
import com.example.clubfund.service.IMessageService;
import com.example.clubfund.service.IReimbursementService;
import com.example.clubfund.utils.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReimbursementServiceImpl extends ServiceImpl<ReimbursementMapper, Reimbursement> implements IReimbursementService {

    @Autowired
    private ActivityApplyMapper activityMapper;

    @Autowired
    private SysUserMapper userMapper; // 用于获取用户信息

    @Autowired
    private IMessageService messageService;

    @Override
    public Result submit(ReimburseSubmitDTO submitDTO) {
        // 1. 获取当前用户身份
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.error(401, "未登录");

        // 2. 验证关联活动
        ActivityApply activity = activityMapper.selectById(submitDTO.getActivityId());
        if (activity == null) {
            return Result.error("关联的活动不存在");
        }

        // --- 核心安全校验 ---
        // 校验A：该活动是否属于当前用户？
        if (!activity.getUserId().equals(userId)) {
            return Result.error("非法操作：您只能报销自己申请的活动");
        }
        // 校验B：活动是否已批准？
        if (activity.getStatus() != 1) { // 1:已批准
            return Result.error("该活动尚未通过审批，无法申请报销");
        }

        // 3. 补充用户信息 (查库最稳妥)
        SysUser user = userMapper.selectById(userId);

        // 4. 组装实体
        Reimbursement reimburse = new Reimbursement();
        BeanUtils.copyProperties(submitDTO, reimburse); // 拷贝金额、凭证URL、说明

        // 强制填充身份信息
        reimburse.setUserId(userId);
        reimburse.setRealName(user.getRealName());
        reimburse.setClubName(user.getClubName());

        reimburse.setStatus(0); // 0:待核销
        reimburse.setCreateTime(LocalDateTime.now());

        save(reimburse);
        return Result.success("报销申请已提交，请等待财务核销");
    }

    @Override
    public Result listMyReimbursements(Integer pageNum, Integer pageSize) {
        Long userId = UserContext.getUserId();
        Page<Reimbursement> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Reimbursement> wrapper = new QueryWrapper<>();

        // 强制只查自己的
        wrapper.eq("user_id", userId);
        // 按时间倒序
        wrapper.orderByDesc("create_time");

        return Result.success(baseMapper.selectPage(page, wrapper));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result audit(ReimburseAuditDTO auditDTO) {
        Reimbursement reimburse = baseMapper.selectById(auditDTO.getId());
        if (reimburse == null) {
            return Result.error("报销单不存在");
        }

        reimburse.setStatus(auditDTO.getStatus());
        reimburse.setAuditComment(auditDTO.getAuditComment());
        reimburse.setAuditTime(LocalDateTime.now());

        baseMapper.updateById(reimburse);

        String statusText = auditDTO.getStatus() == 1 ? "已核销发放" : "已被驳回";
        String title = "财务报销通知";
        String content = String.format("您提交的关于活动【%s】的报销申请（金额：%.2f元）%s。备注：%s",
                reimburse.getActivityId(), // 或者再查一次ActivityName优化体验
                reimburse.getAmount(),
                statusText,
                auditDTO.getAuditComment());

        messageService.sendSysMessage(reimburse.getUserId(), title, content);

        return Result.success("操作成功");
    }

    @Override
    public Result listPage(Integer pageNum, Integer pageSize, Integer status) {
        Page<Reimbursement> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Reimbursement> wrapper = new QueryWrapper<>();

        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");

        return Result.success(baseMapper.selectPage(page, wrapper));
    }
}