package com.zhao.haochidian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhao.haochidian.entity.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
