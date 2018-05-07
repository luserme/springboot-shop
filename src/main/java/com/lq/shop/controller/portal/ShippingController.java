package com.lq.shop.controller.portal;

import com.lq.shop.common.response.Const;
import com.lq.shop.common.response.Const.Page;
import com.lq.shop.common.response.ResultCode;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.entity.ShippingEntity;
import com.lq.shop.entity.UserEntity;
import com.lq.shop.service.IShippingService;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : luqing
 * @date : 2018/4/23 14:52
 */

@RestController
@RequestMapping("/shipping")
public class ShippingController {

    private IShippingService iShippingService;

    @Autowired
    public void setIShippingService(IShippingService iShippingService) {
        this.iShippingService = iShippingService;
    }


    @RequestMapping("/add")
    public ServerResult add(HttpSession session,ShippingEntity shipping){
        UserEntity user = (UserEntity)session.getAttribute(Const.CURRENT_USER);
//        if(user ==null){
//            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),ResultCode.NEED_LOGIN.getDesc());
//        }
        return iShippingService.addShipping(user.getId(),shipping);
    }


    @RequestMapping("/del")
    @ResponseBody
    public ServerResult del(HttpSession session,Integer shippingId){
        UserEntity user = (UserEntity)session.getAttribute(Const.CURRENT_USER);
//        if(user ==null){
//            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),ResultCode.NEED_LOGIN.getDesc());
//        }
        return iShippingService.delShipping(user.getId(),shippingId);
    }

    @RequestMapping("/update")
    @ResponseBody
    public ServerResult update(HttpSession session,ShippingEntity shipping){
        UserEntity user = (UserEntity)session.getAttribute(Const.CURRENT_USER);
//        if(user ==null){
//            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),ResultCode.NEED_LOGIN.getDesc());
//        }
        return iShippingService.updateShipping(user.getId(),shipping);
    }


    @RequestMapping("/select")
    @ResponseBody
    public ServerResult<ShippingEntity> select(HttpSession session,Integer shippingId){
        UserEntity user = (UserEntity)session.getAttribute(Const.CURRENT_USER);
//        if(user ==null){
//            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),ResultCode.NEED_LOGIN.getDesc());
//        }
        return iShippingService.selectShipping(user.getId(),shippingId);
    }


    @RequestMapping("/list")
    @ResponseBody
    public ServerResult list(@RequestParam(value = "pageNum",defaultValue = Page.PAGE_DEFAULT_NUM) Integer pageNum,
        @RequestParam(value = "pageSize",defaultValue = Page.PAGE_DEFAULT_SIZE)int pageSize,
        HttpSession session){
        UserEntity user = (UserEntity)session.getAttribute(Const.CURRENT_USER);
//        if(user ==null){
//            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),ResultCode.NEED_LOGIN.getDesc());
//        }
        return iShippingService.getShippingList(user.getId(),pageNum,pageSize);
    }


}
