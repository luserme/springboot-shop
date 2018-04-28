package com.lq.shop.controller.portal;

import com.lq.shop.common.response.Const;
import com.lq.shop.common.response.Const.AlipayCallback;
import com.lq.shop.common.response.ResultCode;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.entity.UserEntity;
import com.lq.shop.service.IOrderService;
import com.sun.javafx.iio.ios.IosDescriptor;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

        System.out.println(requestParams.toString());

        ServerResult result = iOrderService.aliCallback(requestParams);

        if (result.isSuccess()){
            log.info("支付宝回调成功");
            return AlipayCallback.RESPONSE_SUCCESS;
        }

        log.error("支付宝回调失败");
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

    /**
     * 创建新的订单
     * @param session session
     * @param shippingId 收货地址id
     * @return 创建结果
     */
    @RequestMapping("/create")
    public ServerResult create(HttpSession session,Integer shippingId){
        UserEntity user = (UserEntity) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),
                ResultCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.createOrder(user.getId(),shippingId);
    }

    /**
     * 取消订单
     * @param session session
     * @param orderNo 订单号
     * @return 取消结果
     */
    @RequestMapping("/cancel")
    public ServerResult cancel(HttpSession session,Long orderNo){
        UserEntity user = (UserEntity) session.getAttribute(Const.CURRENT_USER);

        if (user == null){
            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),
                ResultCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.cancel(user.getId(),orderNo);
    }

    /**
     * 获取订单的商品信息
     * @param session session
     * @return 获取结果
     */
    @RequestMapping("/product")
    public ServerResult getOrderProduct(HttpSession session){
        UserEntity user = (UserEntity) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),
                ResultCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.getOrderProduct(user.getId());
    }

    @RequestMapping("/detail")
    public  ServerResult detail(HttpSession session,Long orderNo){
        UserEntity user = (UserEntity) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),
                ResultCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    @RequestMapping("/delivery/goods")
    public  ServerResult deliveryGoods(HttpSession session,Long orderNo){
        UserEntity user = (UserEntity) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),
                ResultCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.deliveryGoods(user.getId(),orderNo);
    }



    @RequestMapping("/list")
    public  ServerResult list(
        HttpSession session,
        @RequestParam(value = "pageNum",defaultValue = Const.Page.PAGE_DEFAULT_NUM) Integer pageNum,
        @RequestParam(value = "pageSize",defaultValue = Const.Page.PAGE_DEFAULT_SIZE) Integer pageSize){

        UserEntity user = (UserEntity) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),
                ResultCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }

}
