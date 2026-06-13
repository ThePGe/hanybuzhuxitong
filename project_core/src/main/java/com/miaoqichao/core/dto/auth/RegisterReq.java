package com.miaoqichao.core.dto.auth;

import lombok.Data;

@Data
public class RegisterReq {
    private String username;
    private String password;
    private String name;
    private Integer role; // 0-学校管理员, 1-学院管理员, 2-辅导员, 3-学生
    private Integer gender; // 1-男, 2-女
    private Integer grade;
    private Long collegeId;
    private String collegeName;
    private String className;
    private String idCard;
}