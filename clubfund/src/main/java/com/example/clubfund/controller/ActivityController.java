package com.example.clubfund.controller;

import com.example.clubfund.common.Result;
import com.example.clubfund.dto.ActivityQueryDTO;
import com.example.clubfund.dto.ActivitySubmitDTO;
import com.example.clubfund.dto.AuditDTO;
import com.example.clubfund.entity.ActivityApply;
import com.example.clubfund.service.IActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private IActivityService activityService;

    // --- 管理员接口 ---

    /**
     * 分页查询所有申请 (管理员)
     */
    @PostMapping("/list")
    public Result list(@RequestBody ActivityQueryDTO queryDTO) {
        // 可以在这里强制 queryDTO.setUserId(null) 确保查全部
        return activityService.selectApplyPage(queryDTO);
    }

    /**
     * 审核申请
     */
    @PostMapping("/audit")
    public Result audit(@RequestBody AuditDTO auditDTO) {
        // 这里应从 Token 获取当前管理员名字赋给 operatorName
        // 为了演示方便，假设前端传了，或者在 Service 里处理
        if(auditDTO.getOperatorName() == null) {
            auditDTO.setOperatorName("管理员");
        }
        return activityService.auditApply(auditDTO);
    }

    /**
     * 获取详情及审批历史
     */
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        return activityService.getDetailWithHistory(id);
    }

    // --- 测试用接口 (模拟用户提交申请) ---
    @PostMapping("/submit")
    public Result submit(@RequestBody ActivityApply apply) {
        return activityService.submitApply(apply);
    }

    /**
     * 用户提交活动申请
     */
    @PostMapping("/create")
    public Result create(@RequestBody ActivitySubmitDTO submitDTO) {
        // 校验参数 (实际项目中建议加上 @Validated)
        if (submitDTO.getTitle() == null || submitDTO.getBudgetAmount() == null) {
            return Result.error("标题和预算不能为空");
        }
        return activityService.createActivity(submitDTO);
    }

    /**
     * 用户查询自己的申请列表 (包含审批状态)
     */
    @PostMapping("/my/list")
    public Result myList(@RequestBody ActivityQueryDTO queryDTO) {
        // 核心安全逻辑：强制覆盖 userId 为当前登录用户
        // 无论前端传什么 userId，这里都只查当前用户的
        Long currentUserId = com.example.clubfund.utils.UserContext.getUserId();
        queryDTO.setUserId(currentUserId);

        // 复用 Service 中已有的分页查询逻辑
        return activityService.selectApplyPage(queryDTO);
    }
}