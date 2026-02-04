package com.example.clubfund.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserProfileVO implements Serializable {
    private String username;   // 账号 (不可改)
    private String realName;   // 真实姓名
    private String studentId;  // 学号
    private String phone;      // 电话
    private String clubName;   // 所属社团
    private Integer role;      // 角色 (用于前端控制菜单显示)
}