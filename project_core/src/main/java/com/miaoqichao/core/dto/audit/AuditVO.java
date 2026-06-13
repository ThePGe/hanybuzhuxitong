package com.miaoqichao.core.dto.audit;

import lombok.Data;

@Data
public class AuditVO {
    private Long id;
    private String studentId;
    private String name;
    private String reason;
    private String status; // pending, approved, rejected
    private String gender; // M, F
    private String college;
    private String major;
    private Long clothingId;
    private String clothingName;
    private Long skuId;
    private String size;
}
