package com.miaoqichao.core.dto.auth;

import lombok.Data;

@Data
public class LoginReq {
    private String username;
    private String password;
}
