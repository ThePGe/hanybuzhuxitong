package com.miaoqichao.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("subsidy_apply")
public class SubsidyApply implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long batchId;
    
    private Long studentId;
    
    private String applyReason;
    
    private Integer counselorStatus; // 0-待审核, 1-通过, 2-不通过
    
    private String counselorReason;
    
    private Boolean counselorSubmit; // 是否已提交至学院
    
    private Integer collegeStatus; // 0-待审核, 1-通过, 2-不通过
    
    private String collegeReason;
    
    private Boolean collegeSubmit; // 是否已提交至学校
    
    private Integer schoolStatus; // 0-待审核, 1-通过, 2-不通过
    
    private String schoolReason;
    
    private Integer finalStatus; // 0-审核中, 1-审核通过, 2-审核拒绝
    
    private Long clothingSkuId;
    
    private Date selectTime;
    
    private Date createTime;
    
    private Date updateTime;
}
