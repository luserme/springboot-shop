package com.lq.shop.service.impl;

import com.lq.shop.common.response.ServerResult;
import com.lq.shop.service.IEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @author luqing
 * @date 2018/05/11 00:25
 */
@Service("iEmailService")
@Slf4j
public class EmailServiceImpl implements IEmailService {

    private JavaMailSender javaMailSender;

    @Autowired
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Value("${mail.send.from}")
    private String sendFrom;

    @Override
    public ServerResult sendSimpleMail(String email, String token){
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(sendFrom);
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject("忘记密码");
            simpleMailMessage.setText("您的验证码是:" + token);
            javaMailSender.send(simpleMailMessage);
            log.info("邮件成功发送至email:{}",email);
            return ServerResult.createBySuccess("邮件发送成功,请注意查收");
        } catch (MailException e) {
            log.error("邮件发送至email异常:{}",email,e);
        }

        return ServerResult.createByErrorMessage("邮件发送失败");
    }
}
