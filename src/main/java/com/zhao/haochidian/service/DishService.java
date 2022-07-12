package com.zhao.haochidian.service;

import com.zhao.haochidian.dto.DishDto;
import com.zhao.haochidian.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);
    DishDto getByIDWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);

    void deleteWithFlavor(List<Long> ids);
}
