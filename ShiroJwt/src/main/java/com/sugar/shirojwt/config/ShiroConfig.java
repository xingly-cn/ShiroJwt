package com.sugar.shirojwt.config;

import org.apache.ibatis.annotations.Mapper;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class ShiroConfig {

    /**
     * 安全管理器
     * @param myRealm
     * @return
     */
    @Bean
    public SecurityManager securityManager(Realm myRealm) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(myRealm);
        return manager;
    }

    /**
     * 认证和角色权限控制
     * @return
     */
    @Bean
    public Realm myRealm() {
        return new ShiroRealm();
    }

    /**
     * 过滤器
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);

        // 基本页面
        factoryBean.setLoginUrl("/");
        factoryBean.setSuccessUrl("/success");
        factoryBean.setUnauthorizedUrl("/500");

        // 拦截规则
        Map<String,String> mp = new HashMap<>();
        mp.put("/login","anon");
        mp.put("/user","authc,roles[admin]");
        mp.put("/**","authc");

        factoryBean.setFilterChainDefinitionMap(mp);
        return factoryBean;
    }
}
