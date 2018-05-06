package com.lq.shop.common.util;

/**
 * @author : luqing
 * @date : 2018/4/19 11:07
 */
public class StringUtils {

    public static String EMPTY = org.apache.commons.lang3.StringUtils.EMPTY;

    public static boolean isBlank(String value) {
        return org.apache.commons.lang3.StringUtils.isBlank(value);
    }

    public static boolean isNotBlank(String value) {
        return org.apache.commons.lang3.StringUtils.isNotBlank(value);
    }

    public static boolean isEmpty(String value){
        return org.apache.commons.lang3.StringUtils.isEmpty(value);
    }

    public static boolean isNotEmpty(String value){
        return org.apache.commons.lang3.StringUtils.isNotEmpty(value);
    }


    public static boolean equals(String forgetToken, String token) {
        return org.apache.commons.lang3.StringUtils.equals(forgetToken,token);
    }
}
