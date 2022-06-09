package com.ft.shiro.entity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回封装
 *
 * @author: 一枚方糖
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private Integer code;
    private String msg;
    private Object data;

    public static Response success(Object data) {
        return new Response(200, "success", data);
    }

    public static Response error() {
        return new Response(400, "error", null);
    }
}
