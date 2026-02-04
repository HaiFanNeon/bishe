package com.example.clubfund.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_message")
public class SysMessage implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;     // 接收人
    private String title;
    private String content;

    private Integer isRead;  // 0:未读 1:已读

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}