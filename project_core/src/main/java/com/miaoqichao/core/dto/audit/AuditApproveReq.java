package com.miaoqichao.core.dto.audit;

import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class AuditApproveReq {
    private List<Long> applicationIds;
    
    @JsonProperty("isPass")
    private Boolean isPass;
    
    private String reason; // 拒绝理由，非必填
}
