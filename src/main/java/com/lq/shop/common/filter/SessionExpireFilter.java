package com.lq.shop.common.filter;

import com.lq.shop.common.response.Const;
import com.lq.shop.common.util.CookieUtil;
import com.lq.shop.common.util.JsonUtil;
import com.lq.shop.common.util.StringUtils;
import com.lq.shop.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 不用框架写(手写)的redis session共享过期拦截器
 * @author luqing
 * @date 2018/05/05 01:18
 */

@Component
public class SessionExpireFilter{
//public class SessionExpireFilter implements Filter{
//  在使用手写方式的 需要实现 Filter 类

    private StringRedisTemplate stringRedisTemplate;

    private RedisTemplate<String, UserEntity> userRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, UserEntity> userRedisTemplate) {
        this.userRedisTemplate = userRedisTemplate;
    }

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        if (StringUtils.isNotEmpty(loginToken)){
            //判断loginToken是否为空
            //如果不为空 符合条件 继续获取user信息

            ValueOperations<String, UserEntity> userOFV = userRedisTemplate.opsForValue();
            UserEntity user = userOFV.get(loginToken);
            if (user != null){
                //如果user不为空，则重置session的时间，即调用设置值的方法
                userRedisTemplate.expire(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIME, TimeUnit.SECONDS);
            }
        }

    }


    public void destroy() {

    }
}
