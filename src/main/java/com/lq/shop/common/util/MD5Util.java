package com.lq.shop.common.util;

import java.security.MessageDigest;
import lombok.extern.log4j.Log4j;

/** 密码md5加密工具
 * @author : luqing
 * @date : 2018/4/19 11:09
 */

@Log4j
public class MD5Util {

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte aB : b) {
            resultSb.append(byteToHexString(aB));
        }

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }

    /**
     * 返回大写MD5
     * @param origin 加密字段
     * @param charsetname 字符集
     * @return 加密后的字符串
     */
    private static String md5encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname)) {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            } else {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
            }
        } catch (Exception exception) {
            log.trace("",exception);
        }
        return resultString.toUpperCase();
    }

    public static String md5EncodeUtf8(String origin) {
        origin = origin + PropertiesUtil.getProperty("password.salt", "");
        return md5encode(origin, "utf-8");
    }

    public static String md5EncodeGBK(String origin) {
        origin = origin + PropertiesUtil.getProperty("password.salt", "");
        return md5encode(origin, "gbk");
    }


    private static final String[] HEX_DIGITS = {"0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

}
