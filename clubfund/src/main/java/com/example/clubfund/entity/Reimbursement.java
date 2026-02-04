package com.example.clubfund.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("reimbursement")
public class Reimbursement implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long activityId; // 必须关联一个已通过的活动
    private Long userId;
    private String realName;
    private String clubName;

    private BigDecimal amount;
    private String voucherUrl; // 前端上传图片后返回的URL
    private String description;

    // 0:待核销 1:已核销 2:驳回
    private Integer status;
    private String auditComment;
    private LocalDateTime auditTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}