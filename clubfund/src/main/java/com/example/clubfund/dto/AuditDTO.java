package com.example.clubfund.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class AuditDTO implements Serializable {
    private Long applyId;

    // 1:批准 2:驳回 3:要求补充
    private Integer action;

    // 审核意见
    private String comment;

    // 审核人姓名 (实际开发中也可从Token获取)
    private String operatorName;
}