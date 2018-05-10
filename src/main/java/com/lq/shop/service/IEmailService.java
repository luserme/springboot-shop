package com.lq.shop.service;

import com.lq.shop.common.response.ServerResult;

/**
 * @author luqing
 * @date 2018/05/11 00:34
 */
public interface IEmailService {

    /**
     * 发送简单邮件接口
     * @param mail 邮箱
     * @param token 验证码
     * @return
     */
    public ServerResult sendSimpleMail(String mail, String token);

}
