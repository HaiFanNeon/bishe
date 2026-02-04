package com.example.clubfund.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("activity_apply")
public class ActivityApply implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String realName;
    private String clubName;
    private String title;
    private String content;
    private BigDecimal budgetAmount;

    // 0:待审核 1:批准 2:驳回 3:补充材料
    private Integer status;
    private String currentAuditComment;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}