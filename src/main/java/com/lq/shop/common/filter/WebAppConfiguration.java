package com.lq.shop.common.filter;

import com.lq.shop.common.filter.interceptor.LogInterceptor;
import com.lq.shop.common.filter.interceptor.NeedAdminInterceptor;
import com.lq.shop.common.filter.interceptor.NeedLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author luqing
 * @date 2018/05/06 22:32
 */
@Configuration
public class WebAppConfiguration extends WebMvcConfigurerAdapter{

    private NeedLoginInterceptor needLoginInterceptor;

    private NeedAdminInterceptor needAdminInterceptor;

    @Autowired
    public void setNeedLoginInterceptor(NeedLoginInterceptor needLoginInterceptor) {
        this.needLoginInterceptor = needLoginInterceptor;
    }

    @Autowired
    public void setNeedAdminInterceptor(NeedAdminInterceptor needAdminInterceptor) {
        this.needAdminInterceptor = needAdminInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //拦截所有请求 打印日志
        registry.addInterceptor(new LogInterceptor())
                .addPathPatterns("/**");

        //拦截需要登录的请求
        registry.addInterceptor(needLoginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/logout")
                .excludePathPatterns("/user/register")
                .excludePathPatterns("/user/forget/**")
                .excludePathPatterns("/manage/user/login")
                .excludePathPatterns("/product/**")
                .excludePathPatterns("/order/alipay/callback")
                .excludePathPatterns("/manage/**");

        //拦截需要管理员
        registry.addInterceptor(needAdminInterceptor)
                .addPathPatterns("/manage/**")
                .excludePathPatterns("/manage/user/login");

    }
}
