package com.miaoqichao.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miaoqichao.core.service.SkuService;
import com.miaoqichao.dao.entity.Sku;
import com.miaoqichao.dao.mapper.SkuMapper;
import org.springframework.stereotype.Service;

@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService {
}
