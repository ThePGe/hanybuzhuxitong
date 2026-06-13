package com.miaoqichao.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miaoqichao.core.service.BatchService;
import com.miaoqichao.dao.entity.Batch;
import com.miaoqichao.dao.mapper.BatchMapper;
import org.springframework.stereotype.Service;

@Service
public class BatchServiceImpl extends ServiceImpl<BatchMapper, Batch> implements BatchService {
}
