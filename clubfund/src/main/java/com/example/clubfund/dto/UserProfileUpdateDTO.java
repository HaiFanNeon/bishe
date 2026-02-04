package com.example.clubfund.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserProfileUpdateDTO implements Serializable {
    // 注意：这里不需要传 ID，ID 由后端从 Token 获取，保证安全
    private String realName;
    private String phone;
    private String clubName; // 如果允许学生自己改社团，否则可以去掉
    private String studentId;
}