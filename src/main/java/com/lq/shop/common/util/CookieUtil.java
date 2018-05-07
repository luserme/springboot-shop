package com.lq.shop.common.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author luqing
 * @date 2018/05/05 01:20
 */

@Slf4j
public class CookieUtil {

    public final static String COOKIE_DOMAIN = "luserme.com";
    public final static String COOKIE_NAME = "login_token";
    public final static String COOKIE_PATH = "/";
    public final static Integer COOKIE_MAX_AGE = 60 * 60 * 24 * 365;
    public final static Boolean COOKIE_HTTP_ONLY = Boolean.TRUE;


    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies){
            log.info("读取 cookieName:{},cookieValue:{}",cookie.getName(),cookie.getName());
            if (StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                log.info("获取到{} 返回cookieName:{},cookieValue:{}",COOKIE_NAME,cookie.getName(),cookie.getValue());
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void  writeLoginToken(HttpServletResponse response,String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath(COOKIE_PATH);
        cookie.setHttpOnly(COOKIE_HTTP_ONLY);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        log.info("写入 cookieName:{},cookieValue:{}", cookie.getName(), cookie.getValue());

        response.addCookie(cookie);
    }

}
