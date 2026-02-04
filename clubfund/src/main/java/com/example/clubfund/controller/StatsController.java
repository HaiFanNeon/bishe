package com.example.clubfund.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.clubfund.common.Result;
import com.example.clubfund.entity.Notice;
import com.example.clubfund.mapper.NoticeMapper;
import com.example.clubfund.service.IStatisticsService;
import com.example.clubfund.vo.DashboardVO;
import com.example.clubfund.vo.ClubStatVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private IStatisticsService statisticsService;

    @Autowired
    private NoticeMapper noticeMapper; // 简单CRUD直接用Mapper，复杂用Service

    // --- 数据统计接口 (返回 VO) ---

    @GetMapping("/dashboard")
    public Result<DashboardVO> dashboard() {
        return statisticsService.getDashboardData();
    }

    @GetMapping("/ranking")
    public Result<List<ClubStatVO>> ranking() {
        return statisticsService.getClubRanking();
    }

    // --- 公示/公告接口 ---

    /**
     * 管理员发布公示
     */
    @PostMapping("/publish")
    public Result publishNotice(@RequestBody Notice notice) {
        noticeMapper.insert(notice);
        return Result.success("公示发布成功");
    }

    /**
     * 用户/管理员查看公示列表 (分页)
     */
    @GetMapping("/public/list")
    public Result listNotices(@RequestParam(defaultValue = "1") Integer pageNum) {
        Page<Notice> page = new Page<>(pageNum, 10);
        QueryWrapper<Notice> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return Result.success(noticeMapper.selectPage(page, wrapper));
    }
}