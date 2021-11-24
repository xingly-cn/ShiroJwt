package com.sugar.shirojwt.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private int code;
    private String msg;
    private Object data;

    public static Result SUCCESS(String msg,Object data) {
        return new Result(200,msg,data);
    }

    public static Result FAIL(String msg) {
        return new Result(500,msg,null);
    }
}
