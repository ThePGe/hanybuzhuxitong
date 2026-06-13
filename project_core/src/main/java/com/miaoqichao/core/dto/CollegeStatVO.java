package com.miaoqichao.core.dto;

import lombok.Data;

@Data
public class CollegeStatVO {
    private String studentId;
    private String name;
    private String gender; // M/F
    private String college;
    private Long clothingId;
    private String clothingName;
    private Long skuId;
    private String size;
    private Integer count;
}
