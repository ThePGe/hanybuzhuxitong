package com.miaoqichao.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_register_apply")
public class RegisterApply implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
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
    
    private Integer status; // 0-待审核, 1-已通过, 2-已驳回
    
    private String rejectReason;
    
    private Date createTime;
    
    private Date updateTime;
}