package com.ft.my_shiro.service;

import com.ft.my_shiro.entity.User;
import org.springframework.stereotype.Service;

/**
 * 服务类
 */
@Service
public interface UserService{
    User getUser(String id);
}
