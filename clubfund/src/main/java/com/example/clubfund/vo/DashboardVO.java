package com.example.clubfund.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DashboardVO implements Serializable {
    // 累计申请通过的预算总额
    private BigDecimal totalBudgetApproved;

    // 累计实际核销（报销）的总金额
    private BigDecimal totalExpenseVerified;

    // 理论结余 (批准预算 - 实际核销)
    // 注意：实际逻辑中，结余可能还需要减去未报销但已批准的额度，这里简化为两者之差
    private BigDecimal totalBalance;

    // 举办活动总场次 (状态为批准的)
    private Integer totalActivities;
}