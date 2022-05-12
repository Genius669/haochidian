package com.zhao.haochidian.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhao.haochidian.entity.OrderDetail;
import com.zhao.haochidian.service.OrderDetailService;
import com.zhao.haochidian.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService{

}




