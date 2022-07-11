package com.zhao.haochidian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhao.haochidian.common.BaseContext;
import com.zhao.haochidian.common.R;
import com.zhao.haochidian.entity.ShoppingCart;
import com.zhao.haochidian.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        //设置用户id，指定哪个用户的购物车
        Long id = BaseContext.getCurrentID();
        shoppingCart.setUserId(id);
        //查询当前菜品/套餐是否在购物车中
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, id);

        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            wrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(wrapper);

        if (one != null) {        //如果已经存在，就在用来的数量上+1
            one.setNumber(one.getNumber() + 1);
            shoppingCartService.updateById(one);
        } else {        //如果不存在则添加到数据库，数量为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }
        return R.success(one);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        //设置用户id，指定哪个用户的购物车
        Long id = BaseContext.getCurrentID();
        shoppingCart.setUserId(id);
        //查询当前菜品/套餐是否在购物车中
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, id);

        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            wrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(wrapper);

        if (one != null) {        //如果已经存在，就在用来的数量上-1
            if(one.getNumber()>1){
                one.setNumber(one.getNumber() - 1);
                shoppingCartService.updateById(one);
            }else {
                shoppingCartService.removeById(one.getId());
            }
        }
        return R.success(one);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentID())
                .orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(wrapper);

        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> wrapper  = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentID());
        shoppingCartService.remove(wrapper);
        return R.success("清空购物车成功");
    }





}
