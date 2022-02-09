package com.xunmall.example.sentinel.api;

import com.xunmall.example.sentinel.model.UserInfo;

/**
 * @Author: wangyj03
 * @Description:
 * @Date: 2022/1/26 16:08
 */
public interface UserBaseService {
    UserInfo getUserInfoById(Long userId) throws Exception;
}
