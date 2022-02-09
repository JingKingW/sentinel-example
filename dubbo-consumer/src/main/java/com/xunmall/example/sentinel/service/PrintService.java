package com.xunmall.example.sentinel.service;


import org.apache.dubbo.common.extension.SPI;

/**
 * @Author: wangyj03
 * @Date: 2021/10/20 11:12
 */
@SPI("impl")
public interface PrintService {

    void printInfo();
}
