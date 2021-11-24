package com.sugar.springboot01.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    /**
     * 安全管理器
     * @return
     */
    @Bean
    public SecurityManager securityManager(Realm myRealm) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(myRealm);
        return manager;
    }

    /**
     * 认证与授权
     * @return
     */
    @Bean
    public Realm myRealm() {
        MyRealm myRealm = new MyRealm();
        return myRealm;
    }

    /**
     * 过滤器 - 什么请求可以访问,不可以访问
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager);
        // 权限页面
        bean.setLoginUrl("/"); // 登陆页面
        bean.setSuccessUrl("/ok"); // 登陆成功页面
        bean.setUnauthorizedUrl("/noauth"); // 没有权限页面
        // 拦截规则
        Map<String,String> filterMap = new LinkedHashMap<String,String>();
        filterMap.put("/login","anon"); // 登陆页面无需验证
        filterMap.put("/logout","logout"); // 登出后清除信息
        // 使用注解来控制,无需在这里写
//        filterMap.put("/admin/**","authc,roles[admin]"); // 管理页面需要验证
//        filterMap.put("/user/**","authc,roles[user]"); // 用户页面需要验证
        filterMap.put("/**","authc");   // 剩余请求需要验证
        bean.setFilterChainDefinitionMap(filterMap);
        return bean;
    }

    /**
     * 启动注解支持 - AOP 支持
     * - @RequiresRoles() 和 @RequiresPermissions()
     * @return creator
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    /**
     * 开启 AOP 支持
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor sourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        // 加入安全管理器
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    /**
     * 启用 Shiro 标签
     * @return
     */
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

}
