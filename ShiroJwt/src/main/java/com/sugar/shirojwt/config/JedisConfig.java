package com.sugar.shirojwt.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableAutoConfiguration
@Slf4j
public class JedisConfig {


    /**
     * Redis 连接池
     * @return
     */
    @Bean
    public JedisPool redisPoolFactory() {
        try {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            JedisPool jedisPool = new JedisPool(jedisPoolConfig, "175.27.243.243", 6379, 10000, "213879");
            log.info("JedisPool 启动成功");
            log.info(jedisPool.getResource().ping());
            return jedisPool;
        }catch (Exception e) {
            log.error("JedisPool 启动失败");
        }
        return null;
    }


}
