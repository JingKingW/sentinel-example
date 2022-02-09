package com.xunmall.example.sentinel.controller;

import com.xunmall.example.sentinel.api.UserBaseService;
import com.xunmall.example.sentinel.model.UserInfo;
import com.xunmall.example.sentinel.service.SpringInitDubbo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wangyj03
 * @Date: 2021/10/20 15:36
 */
@RestController
public class LoginController {

    @Autowired
    private UserBaseService userBaseService;

    @Autowired
    private SpringInitDubbo springInitDubbo;

    @GetMapping(value = "/user/info")
    public String searchUser() throws Exception {
        UserInfo userInfo = userBaseService.getUserInfoById(1L);
        return userInfo.toString();
    }

    @GetMapping(value = "/search/info")
    public String searchInfo() {
        return springInitDubbo.apiDubboInvoker();
    }

}
