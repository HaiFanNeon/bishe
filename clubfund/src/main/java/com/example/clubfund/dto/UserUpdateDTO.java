package com.example.clubfund.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserUpdateDTO implements Serializable {
    private Long id;
    private String realName;
    private String phone;
    private String clubName; // 如果是社团负责人，可能会修改社团名
    private String studentId;
}