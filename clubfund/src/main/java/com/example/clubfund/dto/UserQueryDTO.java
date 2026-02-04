package com.example.clubfund.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserQueryDTO implements Serializable {
    // 当前页码
    private Integer pageNum = 1;
    // 每页条数
    private Integer pageSize = 10;
    // 搜索关键字（用户名或真实姓名）
    private String keyword;
    // 角色筛选
    private Integer role;
}