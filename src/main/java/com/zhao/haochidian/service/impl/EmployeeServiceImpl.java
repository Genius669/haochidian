package com.zhao.haochidian.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhao.haochidian.entity.Employee;
import com.zhao.haochidian.mapper.EmployeeMapper;
import com.zhao.haochidian.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}