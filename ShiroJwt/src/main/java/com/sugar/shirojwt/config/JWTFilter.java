package com.sugar.shirojwt.config;

import com.alibaba.fastjson.JSON;
import com.sugar.shirojwt.common.JWTToken;
import com.sugar.shirojwt.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 自定义过滤器 - 基于 JWT 实现
 */
@Slf4j
public class JWTFilter extends BasicHttpAuthenticationFilter {

    /**
     * 判断用户是否想要登录
     * 检测 Header 中是否有 Authorization 字段
     * @param request
     * @param response
     * @return
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        // 获得 Header 字段内容
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");
        return authorization != null;
    }

    /**
     * 进行登陆 交给 Realm 来验证
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        // 获得 Header 字段内容
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");
        JWTToken token = new JWTToken(authorization);
        // 交给 realm 登陆
        getSubject(request,response).login(token);
        // 没有错误,说明登陆成功,返回 True
        return true;
    }

    /**
     * 永远返回 True
     * 如果在这里返回 False,那么用户啥也看不到
     * 返回 True , 让 controller 的 subject.isAuthenticated() 来判断用户是否登陆
     * 缺点：不能对 get 和 post 分别过滤,但影响不大
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request,response)) {
            try {
                // 用户想登陆,那就登陆
                log.info("========= 请求登陆 =========");
                executeLogin(request,response);
            } catch (Exception e) {
                // 登陆失败
                log.info("========= 登陆失败 =========");
                HttpServletResponse rep = (HttpServletResponse) response;
                rep.setStatus(401);
                rep.setCharacterEncoding("UTF-8");
                rep.setContentType("application/json; charset=utf-8");
                PrintWriter out = null;
                try {
                    out = rep.getWriter();
                    String err = JSON.toJSONString(Result.FAIL("无权访问:" + e));
                    out.append(err);
                } catch (IOException ex) {
                    log.error("登陆失败,返回信息失败 | " + ex);
                }finally {
                    if (out != null) out.close();
                }
            }
        }
        return true;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }


}
