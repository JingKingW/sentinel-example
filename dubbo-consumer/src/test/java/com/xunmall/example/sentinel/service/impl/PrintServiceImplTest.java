package com.xunmall.example.sentinel.service.impl;

import com.xunmall.example.sentinel.service.PrintService;
import org.apache.dubbo.common.extension.ExtensionLoader;

/**
 * @Author: wangyj03
 * @Date: 2021/10/20 11:16
 */
public class PrintServiceImplTest {

    public static void main(String[] args) {
        PrintService defaultExtension = ExtensionLoader.getExtensionLoader(PrintService.class).getDefaultExtension();
        defaultExtension.printInfo();
    }
}