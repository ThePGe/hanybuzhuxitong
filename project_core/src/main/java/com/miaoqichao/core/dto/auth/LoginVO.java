package com.miaoqichao.core.dto.auth;

import lombok.Data;

@Data
public class LoginVO {
    private String token;
    private String role;
    private UserInfoVO userInfo;
}
