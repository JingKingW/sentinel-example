package com.xunmall.example.sentinel.controller;

import com.xunmall.example.sentinel.MainApp;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @Author: wangyj03
 * @Date: 2021/11/2 10:50
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:startup.xml")
@WebAppConfiguration
public class HelloControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();   //构造MockMvc
    }

    @Test
    @SneakyThrows
    public void testSayHi(){
        for (int i = 0; i < 100; i++){
            Thread.sleep(100);
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/hello/tom")).andReturn();
            System.out.println(result.getResponse().getContentAsString());
        }
    }

}