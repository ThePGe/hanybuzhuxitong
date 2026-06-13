package com.miaoqichao.core.dto.auth;

import lombok.Data;
import java.util.Date;

@Data
public class RegisterApplyVO {
    private Long id;
    private String username;
    private String name;
    private Integer role;
    private Integer gender;
    private Integer grade;
    private String collegeName;
    private String className;
    private Integer status;
    private String rejectReason;
    private Date createTime;
}