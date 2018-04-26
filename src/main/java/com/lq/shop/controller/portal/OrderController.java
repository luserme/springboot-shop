package com.lq.shop.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.lq.shop.common.response.Const;
import com.lq.shop.common.response.Const.AlipayCallback;
import com.lq.shop.common.response.ResultCode;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.entity.UserEntity;
import com.lq.shop.service.IOrderService;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lombok.extern.log4j.Log4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.rmi.runtime.Log;

/**
 * @author : luqing
 * @date : 2018/4/26 14:02
 */

@RequestMapping("/order")
@RestController
@Log4j
public class OrderController {

    private IOrderService iOrderService;

    @Autowired
    public void setIOrderService(IOrderService iOrderService) {
        this.iOrderService = iOrderService;
    }

    @RequestMapping("/pay")
    public ServerResult pay(HttpSession session, Long orderNo, HttpServletRequest request){
        UserEntity user = (UserEntity)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),ResultCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo,user.getId(),path);
    }

    @RequestMapping("/alipay/callback")
    public Object alipayCallback(HttpServletRequest request){
        Map<String, String[]> requestParams =  request.getParameterMap();

        ServerResult result = iOrderService.aliCallback(requestParams);

        if (result.isSuccess()){
            return AlipayCallback.RESPONSE_SUCCESS;
        }

        return AlipayCallback.RESPONSE_FAILED;
    }

    @RequestMapping("/pay/status")
    public ServerResult findOrderPayStatus(HttpSession session,Long orderNo){
        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
        if (userEntity == null){
            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),ResultCode.NEED_LOGIN.getDesc());
        }

        ServerResult result = iOrderService.findOrderPayStatus(userEntity.getId(),orderNo);

        if (result.isSuccess()){
            return ServerResult.createBySuccess(true);
        }

        return ServerResult.createBySuccess(false);
    }
}
