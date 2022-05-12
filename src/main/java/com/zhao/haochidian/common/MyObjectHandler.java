package com.zhao.haochidian.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 */
@Component
@Slf4j
public class MyObjectHandler implements MetaObjectHandler {
    //插入操作自动填充
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        /** 每一次请求都是一个线程，一个线程要先后经历如下方法
         * Filter
         * Controller
         * MetaObjectHandler
         */
        metaObject.setValue("createUser", BaseContext.getCurrentID());
        metaObject.setValue("updateUser", BaseContext.getCurrentID());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentID());
    }
}