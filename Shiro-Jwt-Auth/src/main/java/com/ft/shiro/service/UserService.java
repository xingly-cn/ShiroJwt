package com.ft.shiro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ft.shiro.entity.User;


public interface UserService extends IService<User> {
    User getUser(String username);
}
