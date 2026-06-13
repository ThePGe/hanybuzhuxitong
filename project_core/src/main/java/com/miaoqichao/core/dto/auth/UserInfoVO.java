package com.miaoqichao.core.dto.auth;

import lombok.Data;

@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private String name;
    private String role;
    private Boolean isNewStudent; // 针对学生
    private String auditStatus; // pending, approved, rejected
    private Integer finalStatus; // -1:拒绝 0:辅导员待审 1:学院待审 2:学校待审 3:通过
    private String gender; // M, F
    
    private String counselorReason; // 辅导员驳回理由
    private String collegeReason; // 学院驳回理由
    private String schoolReason; // 学校驳回理由
}
