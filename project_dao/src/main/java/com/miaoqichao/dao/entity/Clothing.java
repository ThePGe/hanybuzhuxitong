package com.miaoqichao.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("clothing_style")
public class Clothing implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("batch_id")
    private Long batchId;

    // 款式名称
    private String name;
    
    // 适用性别: M-男, F-女 (与前端对应)
    @TableField(exist = false)
    private String gender;
    
    @TableField("gender_limit")
    @JsonIgnore
    private Integer genderLimit;

    // 款式图片URL
    @TableField("image_url")
    private String imageUrl;
    
    // 尺码推荐说明
    @TableField("size_table_url")
    private String sizeRecommend;
    
    private Date createTime;
    
    @TableField(exist = false)
    private Date updateTime;
    
    public String getGender() {
        if (gender != null) return gender;
        if (genderLimit == null) return null;
        return genderLimit == 1 ? "M" : "F";
    }

    public void setGender(String gender) {
        this.gender = gender;
        if ("M".equals(gender)) {
            this.genderLimit = 1;
        } else if ("F".equals(gender)) {
            this.genderLimit = 2;
        }
    }
}
