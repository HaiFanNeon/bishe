package com.example.clubfund.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;

@Data
public class ReimburseSubmitDTO implements Serializable {
    private Long activityId; // 针对哪个活动报销
    private BigDecimal amount; // 报销多少钱
    private String voucherUrl; // 凭证图片地址
    private String description; // 说明

    // 用户信息通常从Token解析，但也可以由前端传递基础信息
    private Long userId;
    private String realName;
    private String clubName;
}