package com.sugar.shirojwt.uitls;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class JwtUtils {

    // 过期时间 5 分钟
    private static final long EXPIRE_TIME = 5*60*1000;

    /**
     * 生成 token
     * @param username
     * @param password
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String Create (String username,String password) throws UnsupportedEncodingException {
        return JWT.create()
                .withClaim("username",username)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .sign(Algorithm.HMAC256(password));
    }

    /**
     * 校验 token 同时获取 username
     * @param token
     * @param password
     * @return
     * @throws UnsupportedEncodingException
     */
    public static boolean verifyToken(String token,String password) throws UnsupportedEncodingException {
        return JWT.require(Algorithm.HMAC256(password))
                        .build()
                        .verify(token)
                        .getClaim("username").asString() != null;
    }

    /**
     * Token 获得 username
     * @param token
     * @return
     */
    public static String getUsername(String token) {
        return JWT.decode(token).getClaim("username").asString();
    }
}
