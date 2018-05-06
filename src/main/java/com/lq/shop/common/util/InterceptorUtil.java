package com.lq.shop.common.util;

import com.lq.shop.common.response.ServerResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

/**
 * @author luqing
 * @date 2018/05/06 23:11
 */
@Slf4j
public class InterceptorUtil {

    private static final String NO_PARAM_METHOD_NAME = "login";

    public static boolean checkResult(HttpServletResponse response, ServerResult result) throws Exception{
        if (!result.isSuccess()){
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");

            PrintWriter out = response.getWriter();

            out.print(JsonUtil.obj2String(result));
            out.flush();
            out.close();
            return false;
        }

        return true;
    }

    public static void interceptorLog(HttpServletRequest request,HandlerMethod handlerMethod){
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getName();

        StringBuilder requestParamBuffer = new StringBuilder();
        Map<String, String[]> paramMap = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : paramMap.entrySet()){
            String mapKey = entry.getKey();
            String mapValue = Arrays.toString(entry.getValue());
            requestParamBuffer.append(mapKey).append("=").append(mapValue).append("&");
        }

        if (StringUtils.equals(methodName,NO_PARAM_METHOD_NAME)){
            //
            log.info("权限拦截器拦截到请求,className:{},methodName:{}",className,methodName);
            return;
        }

        log.info("权限拦截器拦截到请求,className:{},methodName:{},param:{}",className,methodName,requestParamBuffer.toString());

    }

}
