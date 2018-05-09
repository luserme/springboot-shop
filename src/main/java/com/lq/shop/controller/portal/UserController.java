package com.lq.shop.controller.portal;

import com.lq.shop.common.response.Const;
import com.lq.shop.common.response.ResultCode;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.entity.UserEntity;
import com.lq.shop.service.IUserService;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : luqing
 * @date : 2018/4/19 12:45
 */
@RequestMapping("/user")
@RestController
public class UserController {

    private IUserService iUserService;

    @Autowired
    public void setIUserService(IUserService iUserService) {
        this.iUserService = iUserService;
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ServerResult login(String username,String password,HttpSession session){
        ServerResult result = iUserService.login(username,password);

        if (result.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,result.getData());
        }
        return result;
    }

    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    public ServerResult logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResult.createBySuccess("注销成功");
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public ServerResult register(UserEntity userEntity){
        return iUserService.register(userEntity);
    }

    @RequestMapping(value = "/check/valid",method = RequestMethod.POST)
    public ServerResult checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    @RequestMapping(value = "/info",method = RequestMethod.GET)
    public ServerResult getUserInfo(HttpSession session){
        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
//        if (userEntity != null){
        return ServerResult.createBySuccess(userEntity);
//        }
//        return ServerResult.createByErrorMessage("用户未登录，无法获取当前用户信息");
    }

    @RequestMapping(value = "/forget/question",method = RequestMethod.GET)
    public ServerResult getForgetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value = "/forget/check/answer",method = RequestMethod.POST)
    public ServerResult getForgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value = "/forget/reset/password",method = RequestMethod.PUT)
    public ServerResult forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetRestPassword(username,passwordNew,forgetToken);
    }

    @RequestMapping(value = "/update/info" , method = RequestMethod.PUT)
    public ServerResult updateUserInfo(HttpSession session,UserEntity userEntity){
        UserEntity currentUser = (UserEntity) session.getAttribute(Const.CURRENT_USER);
//        if (currentUser == null){
//            return ServerResult.createByErrorMessage("用户未登录");
//        }

        userEntity.setId(currentUser.getId());
        userEntity.setUsername(currentUser.getUsername());
        ServerResult<UserEntity> result = iUserService.updateUserInfo(userEntity);

        if (result.isSuccess()){
            result.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER,result.getData());
        }
        return  result;
    }

    @RequestMapping(value = "/all/info",method = RequestMethod.GET)
    public ServerResult getAllInfo(HttpSession session){
        UserEntity currentUser = (UserEntity) session.getAttribute(Const.CURRENT_USER);
//        if (currentUser == null){
//            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),"未登录,需要强制登录status=10");
//        }
        return iUserService.getUserInfo(currentUser.getId());
    }

}
