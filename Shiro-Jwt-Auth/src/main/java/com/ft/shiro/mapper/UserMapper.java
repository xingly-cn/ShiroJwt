package com.ft.shiro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ft.shiro.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
