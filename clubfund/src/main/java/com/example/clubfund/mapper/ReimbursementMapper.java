package com.example.clubfund.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.clubfund.entity.Reimbursement;
import com.example.clubfund.vo.ClubStatVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ReimbursementMapper extends BaseMapper<Reimbursement> {

    // 统计所有已核销(status=1)的总金额
    @Select("SELECT IFNULL(SUM(amount), 0) FROM reimbursement WHERE status = 1")
    BigDecimal sumTotalVerifiedAmount();

    // 按社团分组统计花费 (用于图表)
    @Select("SELECT club_name as clubName, SUM(amount) as totalSpent, COUNT(*) as activityCount " +
            "FROM reimbursement WHERE status = 1 GROUP BY club_name ORDER BY totalSpent DESC LIMIT 10")
    List<ClubStatVO> getClubRanking();

}