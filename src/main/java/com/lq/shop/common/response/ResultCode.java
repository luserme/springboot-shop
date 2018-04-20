package com.lq.shop.common.response;

import lombok.Getter;

/**
 * @author : luqing
 * @date : 2018/4/19 11:19
 */
@Getter
public enum ResultCode {

    /**
     * 根据类型返回的code
     */
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT"),
    NEED_LOGIN(10,"NEED_LOGIN");


    private final int code;
    private final String desc;

    ResultCode(int code,String desc){
        this.code = code;
        this.desc = desc;
    }
}
