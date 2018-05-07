package com.lq.shop.common.config;

import com.lq.shop.common.response.Const;
import com.lq.shop.common.util.CookieUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.DefaultCookieSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author luqing
 * @date 2018/05/05 01:01
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = Const.RedisCacheExtime.REDIS_SESSION_EXTIME)
public class RedisSessionConfig{

    @Bean
    public DefaultCookieSerializer defaultCookieSerializer(){
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setDomainName(CookieUtil.COOKIE_DOMAIN);
        defaultCookieSerializer.setUseHttpOnlyCookie(CookieUtil.COOKIE_HTTP_ONLY);
        defaultCookieSerializer.setCookiePath(CookieUtil.COOKIE_PATH);
        defaultCookieSerializer.setCookieMaxAge(CookieUtil.COOKIE_MAX_AGE);

        return defaultCookieSerializer;
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(20);

        return jedisPoolConfig;
    }
}
