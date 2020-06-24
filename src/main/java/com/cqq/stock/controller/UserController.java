package com.cqq.stock.controller;


import com.cqq.stock.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {


    @Autowired
    private UserService userService;
    @GetMapping("user/login")
    public String userLogin(String username, String password) {
        return userService.login(username, password);
    }

    @GetMapping("login")
    public String login(String username, String password) {
        return userService.login(username, password);
    }

    @GetMapping("register")
    public String register(String code, String telephone, String username, String password) {
        return userService.register(code, telephone, username, password);
    }

    @GetMapping("sendCode")
    public String sendCode(String telephone) {
        return userService.sendCode(telephone);
    }
}
