package com.xunmall.example.sentinel.service;

import com.xunmall.example.sentinel.api.DubboServiceOne;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: wangyj03
 * @Date: 2021/11/2 15:16
 */
@Component(value = "dubboServiceOne")
public class DubboServiceOneImpl implements DubboServiceOne {

    private AtomicLong count = new AtomicLong();

    @Override
    public String sayHi(String name) {
        try {
            Thread.sleep(new Random().nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return name;
    }
}
