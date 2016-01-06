package com.store59.box.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.store59.box.model.User;

@Service
public class UserService {

    @Autowired
    private com.store59.box.remoting.UserService userService;

    public List<User> userInfo(String name) {
        User user = new User();
        user.setName(name);
        return userService.getUsers(user);
    }

}
