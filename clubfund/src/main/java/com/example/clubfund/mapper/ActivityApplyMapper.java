package com.example.clubfund.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.clubfund.entity.ActivityApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface ActivityApplyMapper extends BaseMapper<ActivityApply> {

    // 统计所有已批准(status=1)的预算总额
    @Select("SELECT IFNULL(SUM(budget_amount), 0) FROM activity_apply WHERE status = 1")
    BigDecimal sumTotalApprovedBudget();

    // 统计已批准的活动数量
    @Select("SELECT COUNT(*) FROM activity_apply WHERE status = 1")
    Integer countApprovedActivities();

}