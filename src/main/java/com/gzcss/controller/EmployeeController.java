package com.gzcss.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.gzcss.common.R;
import com.gzcss.entity.Employee;
import com.gzcss.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库`
        LambdaQueryWrapper<Employee> queryChainWrapper = new LambdaQueryWrapper<>();
        queryChainWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryChainWrapper);

        //3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("登录失败");
        }
        //4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    ;

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     *
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工..."+employee.toString());

        //获取创建时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 用户状态  1 启动  0 禁用
        // employee.setStatus(1);

        //修改人，创建人
        Long empId = (Long) request.getSession().getAttribute("employee");
        log.info("empid:"+empId);
        employee.setUpdateUser(empId);
        employee.setCreateUser(empId);

        log.info("创建人："+employee.getCreateUser());
        log.info("修改人："+employee.getUpdateUser());
        log.info("创建时间：" + employee.getCreateTime());
        log.info("更新时间：" + employee.getUpdateTime());
        log.info("默认密码加密：" + employee.getPassword());
        log.info("新增成功："+employee.toString());

        employeeService.save(employee);

        return R.success("新增用户成功");
    }

    ;


}
