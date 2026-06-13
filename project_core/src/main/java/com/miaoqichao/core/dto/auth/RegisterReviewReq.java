package com.miaoqichao.core.dto.auth;

import lombok.Data;

@Data
public class RegisterReviewReq {
    private Long id;
    private Boolean approve; // true-通过, false-驳回
    private String rejectReason;
}