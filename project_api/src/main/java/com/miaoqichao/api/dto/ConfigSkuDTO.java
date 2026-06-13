package com.miaoqichao.api.dto;

import com.miaoqichao.dao.entity.Sku;
import lombok.Data;

import java.util.List;

@Data
public class ConfigSkuDTO {
    private Long clothingId;
    private List<Sku> skus;
}
