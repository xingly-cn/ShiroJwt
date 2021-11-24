package com.sugar.springboot01.config;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.HashSet;
import java.util.Set;

/**
 * 自定义 Realm - 实现用户认证和授权
 */
public class MyRealm extends AuthorizingRealm {

    /**
     * 用户认证方法
     * @param Token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken Token) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) Token;
        String username = token.getUsername();
        String password = new String(token.getPassword());
        if ("test".equals(username)) {
            throw new LockedAccountException("账号锁定");
        }
        if (!username.equals("admin") && !username.equals("user")) {
            throw new UnknownAccountException("账号错误");
        }
//        // 密码 MD5 加密
//        HashedCredentialsMatchmder credentialsMatcher = new HashedCredentialsMatcher();
//        credentialsMatcher.setHashAlgorithmName("MD5");
//        credentialsMatcher.setHashIterations(1);
//        this.setCredentialsMatcher(credentialsMatcher);
        /**
         * 创建密码认证对象,Shiro 自动认证
         * 参数1：页面账号,参数2：数据库密码,参数3：当前 Realm 名称
         */
        return new SimpleAuthenticationInfo(username,"e10adc3949ba59abbe56e057f20f883e",getName());
    }

    /**
     * 用户授权方法 - 认证通过后自动执行此方法
     * 从数据库中查询用户的角色和资源,设置到 Shiro 中
     * @param principalCollection
     * @return
     */
    @Override

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("恭喜你认证成功,进入授权页面");
        // 获取用户账号,根据账号从数据库中获取 角色 和 资源
        Object primaryPrincipal = principalCollection.getPrimaryPrincipal();

        // 设置角色
        Set<String> roles = new HashSet<String>();
        if ("admin".equals(primaryPrincipal)) {
            roles.add("admin");
            roles.add("user");
        }
        if ("user".equals(primaryPrincipal)) {
            roles.add("user");
        }

        // 设置资源
        Set<String> resource = new HashSet<String>();
        if ("admin".equals(primaryPrincipal)) {
            resource.add("admin:add");
        }
        if ("user".equals(primaryPrincipal)) {
            resource.add("user:look");
        }

        // 启用角色 和 资源
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roles);
        info.setStringPermissions(resource);

        return info;
    }
}
