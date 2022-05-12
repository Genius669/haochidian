package com.zhao.haochidian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhao.haochidian.common.CustomException;
import com.zhao.haochidian.entity.Category;
import com.zhao.haochidian.entity.Dish;
import com.zhao.haochidian.entity.Setmeal;
import com.zhao.haochidian.mapper.CategoryMapper;
import com.zhao.haochidian.service.CategoryService;
import com.zhao.haochidian.service.DishService;
import com.zhao.haochidian.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        //查询时候关联菜品
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId, id);
        if (dishService.count(dishWrapper) > 0) {
            throw new CustomException("当前分类关联了菜品");
        }
        //查询时候关联套餐
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId, id);
        //判断是否关联
        if (setmealService.count(setmealWrapper) > 0) {
            throw new CustomException("当前分类关联了套餐");
        }
        super.removeById(id);
    }
}