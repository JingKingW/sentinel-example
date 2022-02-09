package com.xunmall.example.sentinel.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @Author: wangyj03
 * @Date: 2021/11/3 16:32
 */
public class UserInfo implements Serializable {

    @NotNull
    @Size(min = 1,max = 20)
    private String username;

    @Min(18)
    @Max(100)
    private Integer age;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "username='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
