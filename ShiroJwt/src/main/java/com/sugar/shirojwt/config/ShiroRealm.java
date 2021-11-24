package com.sugar.shirojwt.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sugar.shirojwt.entity.User;
import com.sugar.shirojwt.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ShiroRealm extends AuthenticatingRealm {

    @Autowired
    UserMapper userMapper;

    /**
     * 认证方法
     * @param auth
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        log.info("===========开始认证===========");
        // 获取用户信息
        UsernamePasswordToken token = (UsernamePasswordToken) auth;
        String username = token.getUsername();
        String password = new String(token.getPassword());

        // 获取用户数据库信息
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,username);
        User user = userMapper.selectOne(wrapper);

        // 错误拦截
        if (user == null) throw new UnknownAccountException("账号不存在");
        if (!user.getPassword().equals(password)) throw new IncorrectCredentialsException("账号或密码错误");
        if (user.getStatus().equals("0")) throw new DisabledAccountException("账号禁用");
        log.info("===========认证成功===========");
        return new SimpleAuthenticationInfo(username,user.getPassword(),getName());

    }
}
