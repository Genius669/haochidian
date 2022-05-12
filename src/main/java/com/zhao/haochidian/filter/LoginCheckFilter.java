package com.zhao.haochidian.filter;


import com.alibaba.fastjson.JSON;
import com.zhao.haochidian.common.BaseContext;
import com.zhao.haochidian.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 过滤器
 * 检查用户是否已经完成登录
 */
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //当前访问uri
        String requestURI = request.getRequestURI();
        //不需要拦截的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
        };
        //处理请求
        boolean check = check(urls, requestURI);
        if (check) {
            filterChain.doFilter(request, response);
            return;
        }
        //网页端判断是否已经登录
        if (request.getSession().getAttribute("employee") != null) {
            Long id = (Long) request.getSession().getAttribute("employee");
            //把用户id存入线程
            BaseContext.setCurrentID(id);
            filterChain.doFilter(request, response);
            return;
        }

        //客户端判断是否已经登录
        if (request.getSession().getAttribute("user") != null) {
            Long id = (Long) request.getSession().getAttribute("user");
            //把用户id存入线程
            BaseContext.setCurrentID(id);
            filterChain.doFilter(request, response);
            return;
        }


        //如果没有登录返回未登录结果，通过输出流的方式向客户端响应页面
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) return true;
        }
        return false;
    }
}
