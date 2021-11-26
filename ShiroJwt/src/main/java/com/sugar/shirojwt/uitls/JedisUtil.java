package com.sugar.shirojwt.uitls;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Slf4j
public class JedisUtil {

    /**
     * 静态注入连接池
     * 重写Shiro的CustomCache无法注入JedisUtil
     */
    public static JedisPool jedisPool;

    @Autowired
    public void setJedisPool(JedisPool jedisPool) {
        JedisUtil.jedisPool = jedisPool;
    }

    /**
     * 对 Jedis 对象所有操作上锁
     * 保证原子性操作
     * @return
     */
    public static synchronized  Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            }else {
                return null;
            }
        }catch (Exception e) {
            log.error("获取 Jedis 异常：" + e.getMessage());
            return null;
        }
    }

    /**
     * 释放 Jedis 对象
     */
    public static void closePool() {
        try {
            jedisPool.close();
        } catch (Exception e) {
            log.error("释放 Jedis 异常：" + e.getMessage());
        }
    }

    /**
     * 获得键值
     * @param key
     * @return
     */
    public static Object getObject(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            byte[] bytes = jedis.get(key.getBytes(StandardCharsets.UTF_8));
            return JSON.parse(bytes);
        }catch (Exception e) {
            log.error("获取键值:" + key + " 异常, 原因：" + e.getMessage());
        }finally {
            if (jedis != null) jedis.close();
        }
        return null;
    }

    /**
     * 设置键值对
     * @param key
     * @param value
     * @return
     */
    public static String setObject(String key,String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.set(key.getBytes(StandardCharsets.UTF_8),JSON.toJSONBytes(value));
        }catch (Exception e) {
            log.error("设置键值异常：" + key + value + e.getMessage());
        }finally {
            if (jedis != null) jedis.close();
        }
        return null;
    }


    /**
     * 设置键值对 + 过期时间
     * @param key
     * @param value
     * @param exp
     * @return
     */
    public static String setObject(String key,String value,int exp) {
        String result = "";
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.set(key.getBytes(StandardCharsets.UTF_8),JSON.toJSONBytes(value));
            if (result.equals("OK")) jedis.expire(key.getBytes(StandardCharsets.UTF_8),exp);
            return result;
        }catch (Exception e) {
            log.error("设置键值过期异常：" + key + value + e.getMessage());
        }finally {
            if (jedis != null) jedis.close();
        }
        return null;
    }

    /**
     * 获取键值 - JSON
     * @param key
     * @return
     */
    public static String getJson(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        }catch (Exception e) {
            log.error("获取键值Json异常" + e.getMessage());
        }finally {
            if (jedis != null) jedis.close();
        }
        return null;
    }

    /**
     * 设置键值对 - JSON
     * @param key
     * @param value
     * @return
     */
    public static String setJson(String key,String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.set(key,value);
        }catch (Exception e) {
            log.error("设置键值Json异常" + e.getMessage());
        }finally {
            if (jedis != null) jedis.close();
        }
        return null;
    }

    /**
     * 设置键值对 + 过期 - JSON
     * @param key
     * @param value
     * @return
     */
    public static String setJson(String key,String value,int exp) {
        String result = "";
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.set(key,value);
            if (result.equals("OK")) jedis.expire(key,exp);
            return result;
        }catch (Exception e) {
            log.error("设置键值Json异常" + e.getMessage());
        }finally {
            if (jedis != null) jedis.close();
        }
        return null;
    }

    /**
     * 删除key
     * @param key
     * @return
     */
    public static Long delKey(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.del(key.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e) {
            log.error("删除键异常：" + key + e.getMessage());
        }finally {
            if (jedis != null) jedis.close();
        }
        return 0L;
    }

    public static Boolean exist(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.exists(key.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e) {
            log.info("查询键异常：" + key + e.getMessage());
        }finally {
            if (jedis != null) jedis.close();
        }
        return false;
    }

    /**
     * 模糊查找key
     * @param key
     * @return
     */
    public static Set<String> keysS(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.keys(key);
        }catch (Exception e) {
            log.info("模糊查询键异常：" + key + e.getMessage());
        }finally {
            if (jedis != null) jedis.close();
        }
        return null;
    }

    /**
     * 模糊查找key
     * @param key
     * @return
     */
    public static Set<byte[]> keysB(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.keys(key.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e) {
            log.info("模糊查询键异常：" + key + e.getMessage());
        }finally {
            if (jedis != null) jedis.close();
        }
        return null;
    }

    public static Long getExpire(String key) {
        Long result = -2L;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.ttl(key);
            return result;
        }catch (Exception e) {
            log.info("获过期时间异常：" + key + e.getMessage());
        }finally {
            if (jedis != null) jedis.close();
        }
        return null;
    }


}
