package com.miaoqichao.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("subsidy_batch")
public class Batch implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("batch_name")
    private String batchName;
    
    // 认定学年
    @TableField("difficulty_year")
    private String year;
    
    // 困难等级
    @TableField("difficulty_level")
    private String level;
    
    // 新生年级
    @TableField("freshman_grade")
    private Integer freshmanGrade;
    
    // 申请开始时间
    @TableField("apply_start_time")
    private Date applyStartTime;

    // 申请截止时间
    @TableField("apply_end_time")
    private Date applyEndTime;
    
    // 选款开始时间
    @TableField("select_start_time")
    private Date selectStartTime;

    // 选款截止时间
    @TableField("select_end_time")
    private Date selectEndTime;
    
    @TableField("status")
    private Integer status;

    private Date createTime;
    
    private Date updateTime;
}
