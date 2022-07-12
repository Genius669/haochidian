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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value="SetmealCatch",key="#setmealDto.categoryId")
    public R<String> saveWithDish(@RequestBody SetmealDto setmealDto) {
/*
        String key = "Setmeal_" + setmealDto.getCategoryId();
        redisTemplate.delete(key);
*/
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
    //@CacheEvict(value="SetmealCatch",allEntries=true)
    public R<String> delete(@RequestParam List<Long> ids) {

        for (Long id : ids) {
            Setmeal one = setmealService.getById(id);
            String key = "SetmealCatch::" + one.getCategoryId();
            redisTemplate.delete(key);
            key = "SetmealDishCatch::" + id;
            redisTemplate.delete(key);
        }

        setmealService.removeWithDish(ids);
        return R.success("删除套餐成功");
    }
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable int status,@RequestParam List<Long> ids){
        for (Long id : ids) {
            Setmeal one = setmealService.getById(id);
            String key = "SetmealCatch::" + one.getCategoryId();
            redisTemplate.delete(key);
            key = "SetmealDishCatch::" + id;
            redisTemplate.delete(key);
        }
        setmealService.changeStatus(status,ids);
        return R.success("修改成功");
    }

    @GetMapping("/list")
    @Cacheable(value = "SetmealCatch",key = "#setmeal.categoryId")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        List<Setmeal> list = null;
/*
        //将套餐的分类ID作为缓存标识
        String key = "Setmeal_" + setmeal.getCategoryId();
        //如果缓存存在直接返回数据
        list = (List<Setmeal>) redisTemplate.opsForValue().get(key);
        if (list != null) return R.success(list);
*/
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        wrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        list = setmealService.list(wrapper);
/*
        redisTemplate.opsForValue().set(key, list, 60, TimeUnit.MINUTES);
*/
        return R.success(list);
    }

    @GetMapping("/dish/{id}")
    @Cacheable(value = "SetmealDishCatch",key = "#id")
    public R<List<SetmealDishDto>> dishList(@PathVariable Long id) {
        List<SetmealDishDto> list = null;
/*
        //将套餐的分类ID作为缓存标识
        String key = "SetmealDish_" + id;
        //如果缓存存在直接返回数据
        list = (List<SetmealDishDto>) redisTemplate.opsForValue().get(key);
        if (list != null) return R.success(list);
*/

        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishService.list(wrapper);

        list = setmealDishes.stream().map(item -> {
            SetmealDishDto dto = new SetmealDishDto();
            BeanUtils.copyProperties(item, dto);
            LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
            dishWrapper.eq(Dish::getId, item.getDishId());
            Dish one = dishService.getOne(dishWrapper);
            dto.setImage(one.getImage());
            //dto.setDish(one);
            return dto;
        }).collect(Collectors.toList());
/*
        redisTemplate.opsForValue().set(key, list, 60, TimeUnit.MINUTES);
*/
        return R.success(list);
    }


}
