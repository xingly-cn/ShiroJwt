package com.sugar.springboot01.Controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DefautController {
    /**
     * 登陆页面
     * @return
     */
    @RequestMapping("/")
    public String index() {
        return "login.html";
    }

    /**
     * 登陆请求
     * @return
     */
    @RequestMapping("/login")
    public String login(String username, String password, Model model) {
        // 获取当前对象
        Subject subject = SecurityUtils.getSubject();
        // 登陆前，先登出
        subject.logout();
        // 没有登陆
        if (!subject.isAuthenticated()) {
            // 创建token
            UsernamePasswordToken token = new UsernamePasswordToken(username,password);
            // 调用 Realm 的认证方法
            try {
                subject.login(token);
            }catch (UnknownAccountException e) {
                model.addAttribute("error","账号不存在");
                return "login.html";
            }catch (LockedAccountException e) {
                model.addAttribute("error","账户锁定");
                return "login.html";
            }catch (IncorrectCredentialsException e) {
                model.addAttribute("error","密码错误");
                return "login.html";
            }
        }
        return "redirect:/ok";
    }

    /**
     * 登出请求
     * @return
     */
    @RequestMapping("/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/";
    }


    /**
     * 登陆成功页面
     * @return
     */
    @RequestMapping("/ok")
    public String success() {
        return "ok.html";
    }

    /**
     * 没权限页面
     * @return
     */
    @RequestMapping("/noauth")
    public String noauth() {
        return "noauth.html";
    }

    /**
     * 管理员页面
     * @return
     */
    @RequestMapping("/admin/look")
    @ResponseBody
    @RequiresRoles(value = {"admin"})
    public String adminLook() {
        return "Admin角色";
    }

    /**
     * 管理员功能 add
     * @return
     */
    @RequestMapping("/admin/add")
    @ResponseBody
    @RequiresRoles(value = {"admin"})
    @RequiresPermissions(value = {"admin:add"})
    public String adminSeek() {
        return "Admin角色,add资源";
    }

    /**
     * 管理员功能 del
     * @return
     */
    @RequestMapping("/admin/del")
    @ResponseBody
    @RequiresRoles(value = {"admin"})
    @RequiresPermissions(value = {"admin:del"})
    public String adminSeek2() {
        return "Admin角色,del资源";
    }

    /**
     * 用户页面
     * @return
     */
    @RequestMapping("/user/look")
    @ResponseBody
    @RequiresRoles(value = {"user"})
    @RequiresPermissions(value = {"user:look"})
    public String userLook() {
        return "User角色,look资源";
    }

    /**
     * 自定义异常拦截 - 拦截 AuthorizationException 异常
     * @return
     */
    @ExceptionHandler(value = {AuthorizationException.class})
    public String permissionError(Throwable throwable) {
        System.out.println("权限认证失败日志 ： " + throwable);
        return "noauth.html";
    }

}
