package com.sugar.shirojwt.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sugar.shirojwt.common.JWTToken;
import com.sugar.shirojwt.entity.User;
import com.sugar.shirojwt.mapper.UserMapper;
import com.sugar.shirojwt.uitls.JwtUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    UserMapper userMapper;

    /**
     * 重写 Token 的载体为 JWTToekn
     * JWTToekn 实现了 AuthenticationToken 接口
     * @param token
     * @return
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 认证方法
     * @param auth
     * @return
     * @throws AuthenticationException
     */
    @SneakyThrows
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        log.info("===========开始认证===========");
        // 获取用户信息
        String token = (String) auth.getCredentials();
        String username = JwtUtils.getUsername(token);

        if (username == null) throw new AuthenticationException("token 不合法");

        // 获取用户数据库信息
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,username);
        User user = userMapper.selectOne(wrapper);

        if (user == null) throw new AuthenticationException(username + "账号不存在");
        if (user.getStatus().equals("0")) throw new LockedAccountException("账户锁定啦");
        if(!JwtUtils.verifyToken(token,user.getPassword())) throw new AuthenticationException("账号或密码错误");

        log.info("===========认证成功===========");
        return new SimpleAuthenticationInfo(token,token,getName());

    }

    /**
     * 角色/权限 授权
     * @param collection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection collection) {
        log.info("===========开始角色授权===========");

        // 查询数据库角色权限
        String token = (String) collection.getPrimaryPrincipal();
        String username = JwtUtils.getUsername(token);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,username);
        User user = userMapper.selectOne(wrapper);

        // 设置角色
        Set<String> roles = new HashSet<>();
        roles.add(user.getRole());

        // 设置权限
        Set<String> permission = new HashSet<>();
        List<String> strings = Arrays.asList(user.getPermission().split(","));
        for (String s : strings) {
            permission.add(s);
        }

        // 启用角色 和 权限
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roles);
        info.setStringPermissions(permission);

        log.info("===========角色授权结束===========");
        return info;
    }
}
