package com.example.clubfund.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.clubfund.common.Result;
import com.example.clubfund.dto.ActivityQueryDTO;
import com.example.clubfund.dto.ActivitySubmitDTO;
import com.example.clubfund.dto.AuditDTO;
import com.example.clubfund.entity.ActivityApply;

public interface IActivityService extends IService<ActivityApply> {
    // 分页查询申请
    Result selectApplyPage(ActivityQueryDTO queryDTO);

    // 提交审核
    Result auditApply(AuditDTO auditDTO);

    // 获取详情（包含审批历史）
    Result getDetailWithHistory(Long id);

    // 社团提交申请 (方便后面用户端功能复用)
    Result submitApply(ActivityApply apply);

    /**
     * 用户端：安全提交活动申请
     */
    Result createActivity(ActivitySubmitDTO submitDTO);
}