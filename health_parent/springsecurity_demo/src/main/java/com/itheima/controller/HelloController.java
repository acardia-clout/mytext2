package com.itheima.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hello")
public class HelloController {

    /**
     * 当前登录用户 如果有add权限 就可以访问add方法
     * @return
     */
    @RequestMapping("/add")
    @PreAuthorize("hasAuthority('CHECKITEM_ADD')")//表示用户必须拥有add权限才能调用当前方法
    public String add(){
        System.out.println("add...");
        return null;
    }

    @RequestMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")//表示用户必须拥有ROLE_ADMIN角色才能调用当前方法
    public String update(){
        System.out.println("update...");
        return null;
    }

    @RequestMapping("/delete")
    @PreAuthorize("hasRole('ABC')")//表示用户必须拥有ABC角色才能调用当前方法
    public String delete(){
        System.out.println("delete...");
        return null;
    }
}