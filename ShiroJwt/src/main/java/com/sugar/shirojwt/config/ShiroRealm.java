package com.sugar.shirojwt.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sugar.shirojwt.entity.User;
import com.sugar.shirojwt.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ShiroRealm extends AuthorizingRealm {

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

    /**
     * 角色授权
     * @param collection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection collection) {
        log.info("===========开始角色授权===========");

        // 查询数据库角色权限
        Object username = collection.getPrimaryPrincipal();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,username);
        User user = userMapper.selectOne(wrapper);

        // 设置角色
        Set<String> roles = new HashSet<>();
        roles.add(user.getRole());

        // 启用角色
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roles);

        log.info("===========角色授权结束===========");
        return info;
    }
}