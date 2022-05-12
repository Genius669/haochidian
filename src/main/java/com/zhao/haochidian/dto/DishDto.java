package com.zhao.haochidian.dto;

import com.zhao.haochidian.entity.Dish;
import com.zhao.haochidian.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
