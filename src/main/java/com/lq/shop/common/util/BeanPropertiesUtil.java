package com.lq.shop.common.util;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * @author : luqing
 * @date : 2018/4/20 14:48
 */
public class BeanPropertiesUtil {

    /**
     * 将为空的properties给找出来,然后返回出来
     * @param src 源对象
     * @return 所有为空的字段
     */
    public static String[] getNullProperties(Object src){
        BeanWrapper srcBean=new BeanWrapperImpl(src);
        PropertyDescriptor[] pds=srcBean.getPropertyDescriptors();
        Set<String> emptyName=new HashSet<>();
        for(PropertyDescriptor p:pds){
            Object srcValue=srcBean.getPropertyValue(p.getName());
            if(srcValue==null) {
                emptyName.add(p.getName());
            }
        }
        String[] result=new String[emptyName.size()];
        return emptyName.toArray(result);
    }

}
