package com.lq.shop.common.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;

import java.io.Serializable;


/**
 * @author : luqing
 * @date : 2018/4/19 11:34
 */

@Getter
@JsonInclude(Include.NON_EMPTY)
public class ServerResult<T> implements Serializable{

    private int status;
    private String msg;
    private T data;

    private ServerResult(int status) {
        this.status = status;
    }

    private ServerResult(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResult(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServerResult(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    /**
     * 将Success字段排除在序列化之外
     * @return 是否成功
     */
    @JsonIgnore
    public boolean isSuccess(){return this.status == ResultCode.SUCCESS.getCode();}

    public static <T> ServerResult<T> createBySuccess(){
        return new ServerResult<T>(ResultCode.SUCCESS.getCode());
    }

    public static <T> ServerResult<T> createBySuccessMessage(String msg){
        return new ServerResult<T>(ResultCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServerResult<T> createBySuccess(T data){
        return new ServerResult<T>(ResultCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResult<T> createBySuccess(String msg,T date){
        return new ServerResult<T>(ResultCode.SUCCESS.getCode(),msg,date);
    }

    public static <T> ServerResult<T> createByError(){
        return new ServerResult<T>(ResultCode.ERROR.getCode(),ResultCode.ERROR.getDesc());
    }

    public static <T> ServerResult<T> createByErrorMessage(String msg){
        return new ServerResult<T>(ResultCode.ERROR.getCode(),msg);
    }

    public static <T> ServerResult<T> createByErrorCodeMessage(Integer errorCode,String msg){
        return new ServerResult<T>(errorCode,msg);
    }
}
