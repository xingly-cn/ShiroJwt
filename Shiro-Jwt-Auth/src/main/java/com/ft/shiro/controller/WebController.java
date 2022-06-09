package com.ft.shiro.controller;

import com.ft.shiro.entity.User;
import com.ft.shiro.entity.common.Response;
import com.ft.shiro.exception.UnauthorizedException;
import com.ft.shiro.service.UserService;
import com.ft.shiro.util.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "用户接口")
public class WebController {

    private static final Logger LOGGER = LogManager.getLogger(WebController.class);

    private UserService userService;

    @Autowired
    public void setService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    @ApiOperation("登陆")
    public Response login(@RequestBody User user) {
        User userBean = userService.getUser(user.getUsername());
        if (userBean.getPassword().equals(user.getPassword())) {
            return new Response(200, "Login success", JWTUtil.sign(user.getUsername(), user.getPassword()));
        } else {
            throw new UnauthorizedException();
        }
    }

    @GetMapping("/article")
    @ApiOperation("文章")
    public Response article() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return new Response(200, "登陆成功", null);
        } else {
            return new Response(200, "游客", null);
        }
    }

    @GetMapping("/require_auth")
    @ApiOperation("是否认证")
    @RequiresAuthentication
    public Response requireAuth() {
        return new Response(200, "已授权成功", null);
    }

    @GetMapping("/require_role")
    @ApiOperation("是否拥有角色权限")
    @RequiresRoles("admin")
    public Response requireRole() {
        return new Response(200, "拥有 admin 权限", null);
    }

    @GetMapping("/require_permission")
    @ApiOperation("是否拥有资源权限")
    @RequiresPermissions(logical = Logical.AND, value = {"view"})
    public Response requirePermission() {
        return new Response(200, "拥有 view 权限", null);
    }

    @RequestMapping(path = "/401")
    @ApiOperation("401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response unauthorized() {
        return new Response(401, "未认证", null);
    }
}
