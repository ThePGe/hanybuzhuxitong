package com.miaoqichao.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miaoqichao.core.service.WhitelistService;
import com.miaoqichao.dao.entity.Whitelist;
import com.miaoqichao.dao.mapper.WhitelistMapper;
import org.springframework.stereotype.Service;

@Service
public class WhitelistServiceImpl extends ServiceImpl<WhitelistMapper, Whitelist> implements WhitelistService {
}
