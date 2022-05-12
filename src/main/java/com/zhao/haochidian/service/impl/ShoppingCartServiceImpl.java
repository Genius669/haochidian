package com.zhao.haochidian.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhao.haochidian.entity.ShoppingCart;
import com.zhao.haochidian.service.ShoppingCartService;
import com.zhao.haochidian.mapper.ShoppingCartMapper;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
        implements ShoppingCartService {

}




