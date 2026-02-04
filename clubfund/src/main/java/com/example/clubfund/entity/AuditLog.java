package com.example.clubfund.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("audit_log")
public class AuditLog implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long applyId;
    private String operatorName;
    private Integer operateAction; // 1:批准 2:驳回 3:补充
    private String comment;
    private LocalDateTime createTime;
}