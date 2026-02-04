package com.example.clubfund.service;

import com.example.clubfund.common.Result;
import com.example.clubfund.vo.DashboardVO;
import com.example.clubfund.vo.ClubStatVO;
import java.util.List;

public interface IStatisticsService {
    // 获取首页仪表盘数据
    Result<DashboardVO> getDashboardData();

    // 获取社团花费排行 (图表数据)
    Result<List<ClubStatVO>> getClubRanking();
}