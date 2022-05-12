package com.zhao.haochidian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhao.haochidian.common.R;
import com.zhao.haochidian.entity.Category;
import com.zhao.haochidian.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    /**
     * 新建菜品/套餐分类
     *
     * @param category 分类对象
     * @return R
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("新增" + (category.getType() == 1 ? "菜品" : "套餐") + "分类成功");
    }


    @GetMapping("/page")
    public R<IPage> page(int page, int pageSize) {
        IPage<Category> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo, wrapper);
        return R.success(pageInfo);
    }


    @DeleteMapping
    public R<String> delete(Long id) {
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改分类成功");
    }

    /**
     * 根据条件查询分类数据
     *
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        //排序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }

}
