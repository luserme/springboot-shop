package com.lq.shop.service;

import com.lq.shop.common.response.ServerResult;
import com.lq.shop.entity.UserEntity;

/**
 * @author : luqing
 * @date : 2018/4/19 12:44
 */
public interface IUserService {

    /**
     * 登录业务
     *
     * @param username 用户名
     * @param password 密码
     * @return result结果
     */
    ServerResult<UserEntity> login(String username, String password);

    /**
     * 注冊业务
     *
     * @param userEntity 用户实体类
     * @return result结果
     */
    ServerResult register(UserEntity userEntity);

    /**
     * 校验用户名和邮箱是否存在
     *
     * @param str  需要校验的内容
     * @param type 校验的类型
     * @return 校验结果
     */
    ServerResult checkValid(String str, String type);

    /**
     * 查找用户的密保问题
     *
     * @param username 用户名
     * @return 查找结果
     */
    ServerResult selectQuestion(String username);

    /**
     * 校验用户问题和密码
     *
     * @param username 用户名
     * @param question 问题
     * @param answer  答案
     * @return 校验结果
     */
    ServerResult checkAnswer(String username, String question, String answer);

    /**
     * 重置密码
     * @param username    用户名
     * @param passwordNew 新密码
     * @param forgetToken 权限码
     * @return 重置结果
     */
    ServerResult forgetRestPassword(String username, String passwordNew, String forgetToken);

    /**
     * 更新用户信息
     * @param userEntity 用户信息实体类
     * @return 更新结果
     */
    ServerResult<UserEntity> updateUserInfo(UserEntity userEntity);

    /**
     * 查找用户完整信息
     * @param id 用户id
     * @return 查找结果
     */
    ServerResult getUserInfo(Integer id);


    /**
     * 校验是否是管理源
     * @param userEntity 用户对象
     * @return 校验结果
     */
    ServerResult checkAdminRole(UserEntity userEntity);


    /** 校验当前用户是否有管理员权限
     * @param userEntity 当前用户信息
     * @return 校验结果
     */
    ServerResult checkAdmin(UserEntity userEntity);

}