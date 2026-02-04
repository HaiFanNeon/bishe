package com.example.clubfund.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_notice")
public class Notice implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;   // 标题，例如：2026年5月经费使用情况公示
    private String content; // 内容，可以是富文本，也可以是管理员手写的总结

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}