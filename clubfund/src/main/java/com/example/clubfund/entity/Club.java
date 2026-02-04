package com.example.clubfund.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 社团实体类
 */
@Data
@TableName("tb_club")
public class Club implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "club_id", type = IdType.AUTO)
    private Long clubId;

    private String clubName;

    private String managerName;

    private String description;

    // 经费余额，涉及金额建议用 BigDecimal
    private BigDecimal balance;

    private LocalDateTime createTime;
}