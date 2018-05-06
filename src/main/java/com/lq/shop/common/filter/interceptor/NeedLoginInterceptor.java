package com.lq.shop.common.filter.interceptor;

import com.lq.shop.common.response.Const;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.common.util.InterceptorUtil;
import com.lq.shop.entity.UserEntity;
import com.lq.shop.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author luqing
 * @date 2018/05/06 22:27
 */
@Component
public class NeedLoginInterceptor implements HandlerInterceptor {


    private IUserService iUserService;

    @Autowired
    public void setIUserService(IUserService iUserService) {
        this.iUserService = iUserService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserEntity userEntity = (UserEntity) request.getSession().getAttribute(Const.CURRENT_USER);

        ServerResult result = iUserService.checkLogin(userEntity);

        return InterceptorUtil.checkResult(response,result);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
