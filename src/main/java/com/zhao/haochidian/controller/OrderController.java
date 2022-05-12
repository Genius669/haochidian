package com.zhao.haochidian.controller;

import com.zhao.haochidian.common.R;
import com.zhao.haochidian.entity.Orders;
import com.zhao.haochidian.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;


    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        ordersService.submit(orders);
        return R.success("支付完成");
    }
}
