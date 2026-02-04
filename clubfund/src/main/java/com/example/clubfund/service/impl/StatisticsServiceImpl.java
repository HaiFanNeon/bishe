package com.example.clubfund.service.impl;

import com.example.clubfund.common.Result;
import com.example.clubfund.mapper.ActivityApplyMapper;
import com.example.clubfund.mapper.ReimbursementMapper;
import com.example.clubfund.service.IStatisticsService;
import com.example.clubfund.vo.ClubStatVO;
import com.example.clubfund.vo.DashboardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StatisticsServiceImpl implements IStatisticsService {

    @Autowired
    private ActivityApplyMapper activityMapper;

    @Autowired
    private ReimbursementMapper reimbursementMapper;

    @Override
    public Result<DashboardVO> getDashboardData() {
        DashboardVO vo = new DashboardVO();

        // 1. 获取批准总预算
        BigDecimal totalBudget = activityMapper.sumTotalApprovedBudget();

        // 2. 获取实际核销总额
        BigDecimal totalSpent = reimbursementMapper.sumTotalVerifiedAmount();

        // 3. 获取活动总数
        Integer count = activityMapper.countApprovedActivities();

        // 4. 计算结余
        BigDecimal balance = totalBudget.subtract(totalSpent);

        vo.setTotalBudgetApproved(totalBudget);
        vo.setTotalExpenseVerified(totalSpent);
        vo.setTotalBalance(balance);
        vo.setTotalActivities(count);

        return Result.success(vo);
    }

    @Override
    public Result<List<ClubStatVO>> getClubRanking() {
        // 直接调用 Mapper 的聚合查询
        List<ClubStatVO> list = reimbursementMapper.getClubRanking();
        return Result.success(list);
    }
}