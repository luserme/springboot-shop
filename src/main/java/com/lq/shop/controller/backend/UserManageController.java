package com.lq.shop.controller.backend;

import com.lq.shop.common.response.Const;
import com.lq.shop.common.response.Const.Role;
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
 * @date : 2018/4/20 15:12
 */

@RequestMapping("/manage/user")
@RestController
public class UserManageController {


    private IUserService iUserService;

    @Autowired
    public void setIUserService(IUserService iUserService) {
        this.iUserService = iUserService;
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ServerResult login(String username,String password,HttpSession session){
        ServerResult<UserEntity> result = iUserService.login(username,password);
        if (result.isSuccess()){
            UserEntity userEntity = result.getData();
            if (userEntity.getRole() == Role.ROLE_ADMIN){
                session.setAttribute(Const.CURRENT_USER,userEntity);
                return result;
            }
        }else {
            return ServerResult.createByErrorMessage("不是管理员，无法登录");
        }

        return result;
    }


}
