package com.store59.box.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store59.box.service.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    
    @RequestMapping("userInfo")
    public Object userInfo(String name) {
       return userService.userInfo(name);
    }
}
