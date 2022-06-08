package com.ft.my_shiro.service.impl;

import com.ft.my_shiro.entity.User;
import com.ft.my_shiro.mapper.UserMapper;
import com.ft.my_shiro.service.UserService;

/**
 * @Author: 一枚方糖
 */
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getUser(String id) {
        return userMapper.selectById(id);
    }
}
