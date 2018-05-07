package com.lq.shop.controller.backend;

import com.lq.shop.common.response.Const;
import com.lq.shop.common.response.ResultCode;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.entity.UserEntity;
import com.lq.shop.service.IOrderService;
import com.lq.shop.service.IUserService;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : luqing
 * @date : 2018/4/28 14:20
 */

@RequestMapping("/manage/order")
@RestController
public class OrderManageController {

    private IUserService iUserService;

    private IOrderService iOrderService;

    @Autowired
    public void setIUserService(IUserService iUserService) {
        this.iUserService = iUserService;
    }

    @Autowired
    public void setIOrderService(IOrderService iOrderService) {
        this.iOrderService = iOrderService;
    }

    @RequestMapping("/list")
    public ServerResult orderList(
        HttpSession session,
        @RequestParam(value = "pageNum",defaultValue = Const.Page.PAGE_DEFAULT_NUM) Integer pageNum,
        @RequestParam(value = "pageSize",defaultValue = Const.Page.PAGE_DEFAULT_SIZE) Integer pageSize
    ){
//        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
//        ServerResult result = iUserService.checkAdmin(userEntity);
//        if (result.isSuccess()){
            return iOrderService.manageList(pageNum,pageSize);
//        }
//        return result;
    }


    @RequestMapping("/detail")
    public  ServerResult orderDetail(HttpSession session,Long orderNo){
        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
        ServerResult result = iUserService.checkAdmin(userEntity);
        if (result.isSuccess()){
            return iOrderService.manageDetail(orderNo);
        }
        return result;
    }

    @RequestMapping("/search")
    public  ServerResult orderSearch(
        HttpSession session,
        Long orderNo,
        @RequestParam(value = "pageNum",defaultValue = Const.Page.PAGE_DEFAULT_NUM) Integer pageNum,
        @RequestParam(value = "pageSize",defaultValue = Const.Page.PAGE_DEFAULT_SIZE) Integer pageSize
    ){
        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
        ServerResult result = iUserService.checkAdmin(userEntity);
        if (result.isSuccess()){
            return iOrderService.manageSearch(orderNo,pageNum,pageSize);
        }
        return result;
    }


    @RequestMapping("/send/goods")
    public  ServerResult orderSendGoods(HttpSession session,Long orderNo){
        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
        ServerResult result = iUserService.checkAdmin(userEntity);
        if (result.isSuccess()){
            return iOrderService.manageSendGoods(orderNo);
        }
        return result;
    }

}
