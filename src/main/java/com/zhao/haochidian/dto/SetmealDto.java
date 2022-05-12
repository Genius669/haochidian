package com.zhao.haochidian.dto;

import com.zhao.haochidian.entity.Setmeal;
import com.zhao.haochidian.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes;
    private String categoryName;
}
