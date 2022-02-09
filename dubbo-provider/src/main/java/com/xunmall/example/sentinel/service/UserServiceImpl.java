package com.xunmall.example.sentinel.service;

import com.xunmall.example.sentinel.api.UserBaseService;
import com.xunmall.example.sentinel.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: wangyj03
 * @Date: 2021/11/3 16:39
 */
@Component(value = "userBaseService")
@Slf4j
public class UserServiceImpl implements UserBaseService {

    private final AtomicInteger count = new AtomicInteger();

    @Override
    public UserInfo getUserInfoById(Long userId) throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("wangyanjing");
        userInfo.setAge(33);
        count.incrementAndGet();
        log.info(count.get() + "");
        if (count.get() % 5 == 0) {
            throw new Exception();
        }
        return userInfo;
    }
}
