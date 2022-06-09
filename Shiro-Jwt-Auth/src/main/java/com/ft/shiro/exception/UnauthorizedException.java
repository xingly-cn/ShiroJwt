package com.ft.shiro.exception;


/**
 * 自定义异常
 *
 * @author: 一枚方糖
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String msg) {
        super(msg);
    }
    public UnauthorizedException() {
        super();
    }
}
