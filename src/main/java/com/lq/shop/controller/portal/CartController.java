package com.lq.shop.controller.portal;

import com.lq.shop.common.response.Const;
import com.lq.shop.common.response.ResultCode;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.entity.UserEntity;
import com.lq.shop.service.ICartService;
import com.lq.shop.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author luqing
 * @date 2018/04/22 17:59
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    private ICartService iCartService;

    @Autowired
    public void setICartService(ICartService iCartService) {
        this.iCartService = iCartService;
    }


    @RequestMapping("/list")
    public ServerResult<CartVO> list(HttpSession session){
        UserEntity user = (UserEntity)session.getAttribute(Const.CURRENT_USER);
//        if(user ==null){
//            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),ResultCode.NEED_LOGIN.getDesc());
//        }
        return iCartService.getCartList(user.getId());
    }

    @RequestMapping("/add")
    public ServerResult<CartVO> add(HttpSession session, Integer count, Integer productId){
        UserEntity user = (UserEntity)session.getAttribute(Const.CURRENT_USER);
//        if(user ==null){
//            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),ResultCode.NEED_LOGIN.getDesc());
//        }
        return iCartService.add(user.getId(),productId,count);
    }

    @RequestMapping("/update")
    public ServerResult<CartVO> update(HttpSession session, Integer count, Integer productId){
        UserEntity user = (UserEntity)session.getAttribute(Const.CURRENT_USER);
//        if(user ==null){
//            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),ResultCode.NEED_LOGIN.getDesc());
//        }
        return iCartService.update(user.getId(),productId,count);
    }

    @RequestMapping("/delete")
    public ServerResult<CartVO> deleteProduct(HttpSession session,String productIds){
        UserEntity user = (UserEntity)session.getAttribute(Const.CURRENT_USER);
//        if(user ==null){
//            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),ResultCode.NEED_LOGIN.getDesc());
//        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }


    @RequestMapping("/select/all")
    public ServerResult<CartVO> selectAll(HttpSession session){
        UserEntity user = (UserEntity)session.getAttribute(Const.CURRENT_USER);
//        if(user ==null){
//            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),ResultCode.NEED_LOGIN.getDesc());
//        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.CHECKED);
    }

    @RequestMapping("/unselect/all")
    public ServerResult<CartVO> unSelectAll(HttpSession session){
        UserEntity user = (UserEntity)session.getAttribute(Const.CURRENT_USER);
//        if(user ==null){
//            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),ResultCode.NEED_LOGIN.getDesc());
//        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.UN_CHECKED);
    }



    @RequestMapping("/select")
    public ServerResult<CartVO> select(HttpSession session,Integer productId){
        UserEntity user = (UserEntity)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),ResultCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);
    }

    @RequestMapping("/unselect")
    public ServerResult<CartVO> unSelect(HttpSession session,Integer productId){
        UserEntity user = (UserEntity)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),ResultCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.UN_CHECKED);
    }


    @RequestMapping("/product/count")
    public ServerResult<Integer> getCartProductCount(HttpSession session){
        UserEntity user = (UserEntity)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResult.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }



}
