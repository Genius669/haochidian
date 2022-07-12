package com.zhao.haochidian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhao.haochidian.common.CustomException;
import com.zhao.haochidian.dto.DishDto;
import com.zhao.haochidian.entity.Dish;
import com.zhao.haochidian.entity.DishFlavor;
import com.zhao.haochidian.service.DishFlavorService;
import com.zhao.haochidian.service.DishService;
import com.zhao.haochidian.mapper.DishMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
        implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        Long id = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map(f -> {
            f.setDishId(id);
            return f;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIDWithFlavor(Long id) {
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(wrapper);

        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表
        this.updateById(dishDto);

        //删除dish_flavor表中已有口味
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(wrapper);

        //更新dish_flavor表
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map(f -> {
            f.setDishId(dishDto.getId());
            return f;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void deleteWithFlavor(List<Long> ids) {
        //dish在售则不可删除
        LambdaQueryWrapper<Dish> selectDish = new LambdaQueryWrapper<>();
        selectDish.in(Dish::getId,ids);
        selectDish.eq(Dish::getStatus,1);
        if (this.count(selectDish)>0) throw new CustomException("菜品正在售卖，不能删除");
        //更新Dish中删除字段
        LambdaUpdateWrapper<Dish> updateDish = new LambdaUpdateWrapper<>();
        updateDish.in(Dish::getId,ids);
        updateDish.set(Dish::getIsDeleted,1);
        this.update(updateDish);
        //更新DishFlavor中删除字段
        LambdaUpdateWrapper<DishFlavor> updateDishFlavor = new LambdaUpdateWrapper<>();
        updateDishFlavor.in(DishFlavor::getDishId,ids);
        updateDishFlavor.set(DishFlavor::getIsDeleted,1);
        dishFlavorService.update(updateDishFlavor);
    }
}




