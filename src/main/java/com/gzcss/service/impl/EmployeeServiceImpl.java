package com.gzcss.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzcss.entity.Employee;
import com.gzcss.mapper.EmployeeMapper;
import com.gzcss.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {

}
