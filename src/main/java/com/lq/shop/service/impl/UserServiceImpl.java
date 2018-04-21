package com.lq.shop.service.impl;

import com.lq.shop.common.response.Const;
import com.lq.shop.common.response.Const.Role;
import com.lq.shop.common.response.ResultCode;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.common.response.TokenCache;
import com.lq.shop.common.util.BeanPropertiesUtil;
import com.lq.shop.common.util.MD5Util;
import com.lq.shop.common.util.StringUtils;
import com.lq.shop.dao.UserRepository;
import com.lq.shop.entity.UserEntity;
import com.lq.shop.service.IUserService;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : luqing
 * @date : 2018/4/19 12:44
 */

@Service("iUserService")
public class UserServiceImpl implements IUserService {


    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ServerResult<UserEntity> login(String username, String password) {
        long resultCount = userRepository.checkUsername(username);
        if (resultCount == 0) {
            return ServerResult.createByErrorMessage("用户名不存在");
        }

        String md5Password = MD5Util.md5EncodeUtf8(password);
        UserEntity userEntity = userRepository.selectLogin(username, md5Password);

        if (userEntity == null) {
            return ServerResult.createByErrorMessage("密码错误");
        }

        userEntity.setPassword(StringUtils.EMPTY);

        return ServerResult.createBySuccess("登录成功", userEntity);
    }


    @Override
    @Transactional(rollbackFor = {})
    public ServerResult register(UserEntity userEntity) {

        ServerResult validResult = this.checkValid(userEntity.getUsername(), Const.USERNAME);
        if (!validResult.isSuccess()) {
            return validResult;
        }

        validResult = this.checkValid(userEntity.getEmail(), Const.EMAIL);
        if (!validResult.isSuccess()) {
            return validResult;
        }

        userEntity.setRole(Const.Role.ROLE_CUSTOMER);
        userEntity.setPassword(MD5Util.md5EncodeUtf8(userEntity.getPassword()));

        UserEntity saveUserEntity = userRepository.save(userEntity);
        if (saveUserEntity == null) {
            return ServerResult.createByErrorMessage("注册失败");
        }

        return ServerResult.createBySuccessMessage("注册成功");
    }


    @Override
    public ServerResult checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(str)) {
            if (Const.USERNAME.equals(type)) {
                long resultCount = userRepository.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResult.createByErrorMessage("用户已存在");
                }
            }

            if (Const.EMAIL.equals(type)) {
                long resultCount = userRepository.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResult.createByErrorMessage("邮箱已存在");
                }
            }
        } else {
            return ServerResult.createByErrorMessage("参数错误");
        }
        return ServerResult.createBySuccessMessage("校验成功");
    }


    @Override
    public ServerResult selectQuestion(String username) {
        ServerResult validResult = this.checkValid(username, Const.USERNAME);
        if (validResult.isSuccess()) {
            return ServerResult.createByErrorMessage("用户不存在");
        }
        //查找问题
        String question = userRepository.findQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResult.createBySuccess(question);
        }
        return ServerResult.createByErrorMessage("找回密码的问题为空");
    }

    @Override
    public ServerResult checkAnswer(String username, String question, String answer) {
        long resultCount = userRepository.checkAnswer(username, question, answer);

        if (resultCount > 0) {
            //用户名 问题 答案匹配
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResult.createBySuccess(forgetToken);
        }

        return ServerResult.createByErrorMessage("答案错误");
    }

    @Override
    @Transactional(rollbackFor = {})
    public ServerResult forgetRestPassword(String username, String passwordNew,
        String forgetToken) {

        if (StringUtils.isBlank(forgetToken)) {
            return ServerResult.createByErrorMessage("参数错误，请传递token");
        }

        ServerResult validResult = this.checkValid(username, Const.USERNAME);
        if (validResult.isSuccess()) {
            return ServerResult.createByErrorMessage("用户不存在");
        }

        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResult.createByErrorMessage("token无效或者过期,请重新获取");
        }

        if (StringUtils.equals(forgetToken, token)) {
            String md5Password = MD5Util.md5EncodeUtf8(passwordNew);

            //通过更新对象来实现数据更新
            UserEntity resultUser = userRepository.findByUsername(username);
            resultUser.setPassword(md5Password);
            UserEntity userEntity = userRepository.saveAndFlush(resultUser);

            if (userEntity != null) {
                return ServerResult.createBySuccess("重置密码成功");
            }
            return ServerResult.createByErrorMessage("重置密码失败,请重试");
        }

        return ServerResult.createByErrorMessage("错误的token,请核对后重置");

    }

    @Override
    public ServerResult<UserEntity> updateUserInfo(UserEntity userEntity) {

        int resultCount = userRepository.checkEmailByUserId(userEntity.getEmail(),userEntity.getId());

        if (resultCount > 0){
            return ServerResult.createByErrorMessage("email已存在，请更换email重试");
        }

        UserEntity updateUser = userRepository.findOne(userEntity.getId());

        BeanUtils.copyProperties(userEntity,updateUser, BeanPropertiesUtil.getNullProperties(userEntity));

        System.out.println(updateUser);


        UserEntity resultUser = userRepository.save(updateUser);
        if (resultUser != null){
            return ServerResult.createBySuccess("更新信息成功",updateUser);
        }
        return ServerResult.createByErrorMessage("更新个人信息失败");
    }

    @Override
    public ServerResult getUserInfo(Integer id) {

        UserEntity userEntity = userRepository.findOne(id);
        if (userEntity == null){
            return ServerResult.createByErrorMessage("找不到当前用户");
        }

        userEntity.setPassword(StringUtils.EMPTY);
        return ServerResult.createBySuccess(userEntity);
    }

    @Override
    public ServerResult checkAdminRole(UserEntity userEntity){
        if (userEntity != null && userEntity.getRole() == Role.ROLE_ADMIN){
            return ServerResult.createBySuccess();
        }

        return ServerResult.createByError();
    }


    @Override
    public ServerResult checkAdmin(UserEntity userEntity){
        if (userEntity == null){
            return ServerResult.createByErrorCodeMessage(ResultCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }

        if (checkAdminRole(userEntity).isSuccess()){
            return ServerResult.createBySuccess();
        }else {
            return ServerResult.createByErrorMessage("您没有权限操作");
        }


    }

}
