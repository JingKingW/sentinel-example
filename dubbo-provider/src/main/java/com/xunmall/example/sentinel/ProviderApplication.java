package com.xunmall.example.sentinel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author: wangyj03
 * @Date: 2021/11/2 16:51
 */
public class ProviderApplication {

    private static Logger logger = LoggerFactory.getLogger(ProviderApplication.class);

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("startup.xml");
        context.start();
        logger.info("================== startup ok!===================");
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
