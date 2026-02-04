package com.example.clubfund.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 登录参数传输对象
 * 用于接收前端登录请求中的用户名和密码
 */
@Data
public class LoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}