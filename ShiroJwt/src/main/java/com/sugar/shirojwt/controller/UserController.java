package com.sugar.shirojwt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sugar.shirojwt.common.Result;
import com.sugar.shirojwt.entity.User;
import com.sugar.shirojwt.mapper.UserMapper;
import com.sugar.shirojwt.uitls.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;

/**
 * @author sugar
 * @since 2021-11-24
 */
@Controller
@Slf4j
public class UserController {

    @Autowired
    UserMapper userMapper;

    @RequestMapping("/")
    public String index() {
        Subject subject = SecurityUtils.getSubject();
        // 已经登录直接跳转成功页面
        if (subject.isAuthenticated()) return "redirect:/success";
        return "login.html";
    }

    @ResponseBody
    @RequestMapping("/success")
    public String success() {
        return "JWT获取地方";
    }

    @ResponseBody
    @RequestMapping("/500")
    public String error() {
        return "500";
    }

    @ResponseBody
    @RequestMapping("/user")
    @RequiresRoles("admin")
    @RequiresPermissions({"add","del","view"})
    public Result getAllUser() {
        Page<User> page = new Page<>(1,5);
        return Result.SUCCESS("(三权限)获取用户列表",userMapper.selectPage(page, null));
    }


    @ResponseBody
    @RequestMapping("/login")
    public Result login(String username, String password) throws UnsupportedEncodingException {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,username);
        User user = userMapper.selectOne(wrapper);
        if (!user.getPassword().equals(password)) throw new UnauthenticatedException();
        return Result.SUCCESS("登陆成功", JwtUtils.Create(username,password));
    }


    @RequestMapping("/logout")
    public String logout() {
        // 获取当前会话
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        log.info("============退出成功============");
        return "login.html";
    }

    @ResponseBody
    @RequestMapping("/article")
    @RequiresPermissions("view")
    public Result article() {
        return Result.SUCCESS("(view)需要登陆才可以看的文章",null);
    }

    /**
     * Shiro 角色/权限 认证失败 全局捕获
     * @param throwable
     * @return
     */
    @ResponseBody
    @ExceptionHandler(ShiroException.class)
    public Result rolesError(Throwable throwable) {
        log.info("=====权限认证失败:" + throwable.toString() + "=====");
        return Result.FAIL(throwable.toString());
    }
}
