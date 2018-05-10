package com.lq.shop.common.util;

import java.util.Random;

/**
 * @author luqing
 * @date 2018/05/11 00:56
 */
public class CheckCodeUtil {

    public static String createCheckCode(Integer len){
        StringBuffer stringBuffer = new StringBuffer();
        Random random = new Random();

        for (int i = 0; i<len;i++) {
            int ran = random.nextInt(3);
            if (ran==0){
                //十个数字0-9
                stringBuffer.append ((char) (random.nextInt(10) + 48));
            }

            if (ran==1){
                //26个小写a-z
                stringBuffer.append((char) (random.nextInt(26) + 65)) ;
            }

            if (ran==2){
                //26个大写A-Z
                stringBuffer.append((char) (random.nextInt(26) + 97));
            }
        }

        return stringBuffer.toString();
    }


}
