package com.store59.box.controller;

import org.springframework.web.bind.annotation.RestController;

import com.store59.box.service.UserService;

@RestController
public class UserController {

    private UserService userService;

    public Object userInfo(String name) {
       return userService.userInfo(name);
    }
}
