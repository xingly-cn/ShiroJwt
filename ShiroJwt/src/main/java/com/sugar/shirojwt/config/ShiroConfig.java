package com.sugar.shirojwt.config;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
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

        // 关闭 Shiro 自带 Session
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator evaluator = new DefaultSessionStorageEvaluator();
        evaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(evaluator);
        manager.setSubjectDAO(subjectDAO);

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

        // 添加自定义过滤器
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt",new JWTFilter());
        factoryBean.setFilters(filterMap);

        factoryBean.setSecurityManager(securityManager);
        factoryBean.setUnauthorizedUrl("/500");

        // 自定义拦截规则
        Map<String,String> filterRuleMap = new HashMap<>();
        filterRuleMap.put("/**","jwt");
        filterRuleMap.put("/500","anon");
        factoryBean.setFilterChainDefinitionMap(filterRuleMap);
        return factoryBean;
    }

    /**
     * 启用权限注解
     * @return
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    /**
     * 生命周期控制
     * @return
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开始 AOP 支持
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


}
