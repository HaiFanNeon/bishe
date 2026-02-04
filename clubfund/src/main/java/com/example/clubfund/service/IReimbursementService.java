package com.example.clubfund.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.clubfund.common.Result;
import com.example.clubfund.dto.ReimburseAuditDTO;
import com.example.clubfund.dto.ReimburseSubmitDTO;
import com.example.clubfund.entity.Reimbursement;

public interface IReimbursementService extends IService<Reimbursement> {
    // 用户端：安全提交报销
    Result submit(ReimburseSubmitDTO submitDTO);

    // 管理员：审核核销 (保持不变)
    Result audit(ReimburseAuditDTO auditDTO);

    // 管理员：列表 (保持不变)
    Result listPage(Integer pageNum, Integer pageSize, Integer status);

    // 新增：用户端查询自己的报销记录
    Result listMyReimbursements(Integer pageNum, Integer pageSize);
}