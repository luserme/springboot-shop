package com.lq.shop.common.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author luqing
 * @date 2018/05/06 00:58
 */

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler implements HandlerExceptionResolver{

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        log.error("{} 发生异常",httpServletRequest.getRequestURI(),e);
        ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView());

        modelAndView.addObject("status",ResultCode.ERROR.getCode());
        modelAndView.addObject("msg","接口异常，请联系管理员");
        modelAndView.addObject("data",e.toString());
        return modelAndView;
    }
}
