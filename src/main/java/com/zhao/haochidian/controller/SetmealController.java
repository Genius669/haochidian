package com.zhao.haochidian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhao.haochidian.common.R;

//import com.zhao.haochidian.dto.SetmealDishDto;
import com.zhao.haochidian.dto.SetmealDishDto;
import com.zhao.haochidian.dto.SetmealDto;
import com.zhao.haochidian.entity.Category;
import com.zhao.haochidian.entity.Dish;
import com.zhao.haochidian.entity.Setmeal;
import com.zhao.haochidian.entity.SetmealDish;
import com.zhao.haochidian.service.CategoryService;
import com.zhao.haochidian.service.DishService;
import com.zhao.haochidian.service.SetmealDishService;
import com.zhao.haochidian.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private DishService dishService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> saveWithDish(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("套餐添加成功");
    }

    /**
     * 分页数据
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page(page, pageSize);
        Page<SetmealDto> dtoPage = new Page(page, pageSize);

        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Setmeal::getName, name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage, wrapper);

        BeanUtils.copyProperties(setmealPage, dtoPage, "records");
        List<SetmealDto> list = setmealPage.getRecords().stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Category category = categoryService.getById(item.getCategoryId());
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);

    }

    /**
     * 删除套餐
     * 同时删除套餐菜品关系表的菜品数据
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.removeWithDish(ids);
        return R.success("删除套餐成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        wrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(wrapper);
        return R.success(list);
    }

    @GetMapping("/dish/{id}")
    public R<List<SetmealDishDto>> dishList(@PathVariable Long id) {
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishService.list(wrapper);

        List<SetmealDishDto> list = setmealDishes.stream().map(item -> {
            SetmealDishDto dto = new SetmealDishDto();
            BeanUtils.copyProperties(item, dto);
            LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
            dishWrapper.eq(Dish::getId, item.getDishId());
            Dish one = dishService.getOne(dishWrapper);
            dto.setImage(one.getImage());
//            dto.setDish(one);
            return dto;
        }).collect(Collectors.toList());
        return R.success(list);
    }


}
