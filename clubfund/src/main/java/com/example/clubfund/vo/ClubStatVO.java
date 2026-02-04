package com.example.clubfund.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ClubStatVO implements Serializable {
    // 社团名称 (X轴)
    private String clubName;

    // 花费金额 (Y轴)
    private BigDecimal totalSpent;

    // 活动数量 (可选，用于气泡图等)
    private Integer activityCount;
}