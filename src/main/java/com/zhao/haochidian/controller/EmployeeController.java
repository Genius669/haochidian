package com.zhao.haochidian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhao.haochidian.common.R;
import com.zhao.haochidian.entity.Employee;
import com.zhao.haochidian.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //取密码加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //根据username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //判空判否
        if (emp == null) return R.error("登录失败");
        if (!emp.getPassword().equals(password)) return R.error("登录失败");
        if (emp.getStatus() == 0) return R.error("登录失败，账号已被禁用");
        //写入缓存
        request.getSession().setAttribute("employee", emp.getId());
        //登录成功
        return R.success(emp);
    }

    /**
     * 员工退出登录
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出登录");
    }


    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setCreateUser(id);
//        employee.setUpdateUser(id);
        employeeService.save(employee);
        return R.success("保存成功");
    }

    /**
     * 修改员工信息
     *
     * @param request  客户端对象
     * @param employee 根据参数封装员工对象
     * @return R
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
//        employee.setUpdateUser((long) request.getSession().getAttribute("employee"));
//        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success("员工修改成功");
    }


    /**
     * 分页查询
     *
     * @param page     当前页数
     * @param pageSize 页面尺寸
     * @param name     查询条件
     * @return R
     */
    @GetMapping("/page")
    public R<IPage> page(int page, int pageSize, String name) {
        //分页构造器
        IPage pageInfo = new Page(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper();
        //添加过滤条件
        wrapper.like(name != "" && name != null, Employee::getName, name);
        wrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo, wrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id查询员工信息
     *
     * @param id 根据路径参数id拿到查询员工的数据
     * @return R
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) return R.success(employee);
        return R.error("没有查询到对应员工信息");
    }

}

