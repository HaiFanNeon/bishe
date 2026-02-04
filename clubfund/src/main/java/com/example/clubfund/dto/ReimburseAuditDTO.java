package com.example.clubfund.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ReimburseAuditDTO implements Serializable {
    private Long id; // 报销单ID
    private Integer status; // 1:通过(核销) 2:驳回
    private String auditComment; // 备注
}