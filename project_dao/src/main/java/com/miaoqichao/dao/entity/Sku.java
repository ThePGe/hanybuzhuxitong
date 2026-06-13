package com.miaoqichao.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("clothing_sku")
public class Sku implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    // 对应的款式ID
    @TableField("style_id")
    private Long clothingId;
    
    // 尺码
    @TableField("size_name")
    private String size;
    
    // 商品编码
    @TableField("product_code")
    private String skuCode;
    
    private Date createTime;
    
    @TableField(exist = false)
    private Date updateTime;
}
