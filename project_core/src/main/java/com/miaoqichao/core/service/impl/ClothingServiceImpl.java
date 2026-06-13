package com.miaoqichao.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miaoqichao.core.service.ClothingService;
import com.miaoqichao.dao.entity.Clothing;
import com.miaoqichao.dao.mapper.ClothingMapper;
import org.springframework.stereotype.Service;

@Service
public class ClothingServiceImpl extends ServiceImpl<ClothingMapper, Clothing> implements ClothingService {
}
