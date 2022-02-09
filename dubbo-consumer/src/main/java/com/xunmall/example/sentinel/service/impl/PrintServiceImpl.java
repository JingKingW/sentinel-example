package com.xunmall.example.sentinel.service.impl;

import com.xunmall.example.sentinel.service.PrintService;

/**
 * @Author: wangyj03
 * @Date: 2021/10/20 11:13
 */
public class PrintServiceImpl implements PrintService {
    @Override
    public void printInfo() {
        System.out.println("Hello SPI");
    }
}
