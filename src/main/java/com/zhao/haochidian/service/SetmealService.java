package com.zhao.haochidian.service;

import com.zhao.haochidian.dto.SetmealDto;
import com.zhao.haochidian.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);
}
