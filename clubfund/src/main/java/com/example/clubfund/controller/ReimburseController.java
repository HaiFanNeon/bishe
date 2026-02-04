package com.example.clubfund.controller;

import com.example.clubfund.common.Result;
import com.example.clubfund.dto.ReimburseAuditDTO;
import com.example.clubfund.dto.ReimburseSubmitDTO;
import com.example.clubfund.service.IReimbursementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reimburse")
public class ReimburseController {

    @Autowired
    private IReimbursementService reimburseService;

    /**
     * 用户提交报销申请
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody ReimburseSubmitDTO submitDTO) {
        return reimburseService.submit(submitDTO);
    }

    /**
     * 管理员分页查看报销列表
     */
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false) Integer status) {
        return reimburseService.listPage(pageNum, pageSize, status);
    }

    /**
     * 管理员执行核销/驳回
     */
    @PostMapping("/audit")
    public Result audit(@RequestBody ReimburseAuditDTO auditDTO) {
        return reimburseService.audit(auditDTO);
    }
}