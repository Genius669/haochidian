package com.zhao.haochidian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhao.haochidian.common.CustomException;
import com.zhao.haochidian.dto.SetmealDto;
import com.zhao.haochidian.entity.Setmeal;
import com.zhao.haochidian.entity.SetmealDish;
import com.zhao.haochidian.service.SetmealDishService;
import com.zhao.haochidian.service.SetmealService;
import com.zhao.haochidian.mapper.SetmealMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存setmeal
        this.save(setmealDto);
        //保存setmeal_dish
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
//        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
//        setmealWrapper.in(Setmeal::getId, ids);
//        setmealWrapper.eq(Setmeal::getStatus, 1);
//        int count = this.count(setmealWrapper);
//        if (count > 0) throw new CustomException("套餐正在售卖中，不能删除");


        //删除setmeal表中数据
        LambdaUpdateWrapper<Setmeal> setmealUpdate = new LambdaUpdateWrapper<>();
        setmealUpdate.in(Setmeal::getId, ids);
        setmealUpdate.set(Setmeal::getStatus, 0);
        setmealUpdate.set(Setmeal::getIsDeleted, 1);
        this.update(setmealUpdate);


        //产出setmealdish表中的数据
        LambdaUpdateWrapper<SetmealDish> dishUpdate = new LambdaUpdateWrapper<>();
        dishUpdate.in(SetmealDish::getSetmealId, ids);
        dishUpdate.set(SetmealDish::getIsDeleted, 1);
        setmealDishService.update(dishUpdate);


    }
}




