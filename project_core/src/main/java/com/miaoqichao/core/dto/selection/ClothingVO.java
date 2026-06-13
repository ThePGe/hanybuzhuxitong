package com.miaoqichao.core.dto.selection;

import com.miaoqichao.dao.entity.Clothing;
import com.miaoqichao.dao.entity.Sku;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClothingVO extends Clothing {
    private List<Sku> skus;
}
