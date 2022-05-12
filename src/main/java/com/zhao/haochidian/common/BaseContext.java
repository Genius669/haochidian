package com.zhao.haochidian.common;

public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentID(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentID() {

        return threadLocal.get();
    }

}
