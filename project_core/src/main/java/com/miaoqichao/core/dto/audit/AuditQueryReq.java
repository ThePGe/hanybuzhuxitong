package com.miaoqichao.core.dto.audit;

import lombok.Data;

@Data
public class AuditQueryReq {
    private Integer page = 1;
    private Integer size = 20;
    
    // status: "pending", "approved", "rejected"
    private String status;
}
