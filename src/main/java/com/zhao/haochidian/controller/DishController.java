package com.zhao.haochidian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhao.haochidian.common.R;
import com.zhao.haochidian.dto.DishDto;
import com.zhao.haochidian.entity.Category;
import com.zhao.haochidian.entity.Dish;
import com.zhao.haochidian.entity.DishFlavor;
import com.zhao.haochidian.service.CategoryService;
import com.zhao.haochidian.service.DishFlavorService;
import com.zhao.haochidian.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        String key = "dish_"+dishDto.getCategoryId();
        redisTemplate.delete(key);
        dishService.saveWithFlavor(dishDto);
        return R.success("添加菜品成功");
    }


    /**
     * 菜品信息分页查询
     *
     * @param page     页码
     * @param pageSize 页面尺寸
     * @param name     菜品名称
     * @return page
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        /* 根据条件查询Dish列表 */
        Page<Dish> dishPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper();
        wrapper.like(name != null, Dish::getName, name);
        wrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage, wrapper);

        /* 对象拷贝 dishPage->dishDtoPage,忽略records字段 */
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        /* 单独处理records内的数据，将分类名称写入dto对象 */
        List<Dish> records = dishPage.getRecords();

        List<DishDto> list = records.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryID = item.getCategoryId();
            Category category = categoryService.getById(categoryID);
            if (category != null) dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList());

        /* 将list回写dtopage */
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> getByID(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIDWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        String key = "dish_"+dishDto.getCategoryId();
        redisTemplate.delete(key);
        dishService.updateWithFlavor(dishDto);
        return R.success("菜品更新成功");
    }


    /**
     * 根据条件查询对应菜品
     *
     * @param dish
     * @return
     */
/*    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .eq(Dish::getStatus, 1);
        wrapper.orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(wrapper);
        return R.success(list);
    }*/
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> list = null;

        String key = "dish_" + dish.getCategoryId();
        //先查询缓存是否已存
        list = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //如果缓存存在直接返回
        if (list != null) return R.success(list);

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId()).eq(Dish::getStatus, 1).orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(wrapper);

        list = dishList.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) dishDto.setCategoryName(category.getName());

            LambdaQueryWrapper<DishFlavor> flavorWrapper = new LambdaQueryWrapper<>();
            flavorWrapper.eq(DishFlavor::getDishId, item.getId());
            dishDto.setFlavors(dishFlavorService.list(flavorWrapper));
            return dishDto;
        }).collect(Collectors.toList());

        //如果缓存不存在，查询数据库并存入缓存
        redisTemplate.opsForValue().set(key, list, 60, TimeUnit.MINUTES);
        return R.success(list);
    }


}
