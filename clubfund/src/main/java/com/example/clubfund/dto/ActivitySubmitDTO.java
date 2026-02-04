package com.example.clubfund.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ActivitySubmitDTO implements Serializable {

    // 标题
    private String title;

    // 策划详情
    private String content;

    // 预算金额
    private BigDecimal budgetAmount;
}