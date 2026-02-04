package com.example.clubfund.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ActivityQueryDTO implements Serializable {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    private String keyword; // 搜标题或社团名
    private Integer status; // 按状态筛选
    private Long userId; // 如果是用户端查自己的，传这个ID
}