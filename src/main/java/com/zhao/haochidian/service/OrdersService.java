package com.zhao.haochidian.service;

import com.zhao.haochidian.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;


public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}
