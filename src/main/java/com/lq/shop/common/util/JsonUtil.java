package com.lq.shop.common.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.text.SimpleDateFormat;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : luqing
 * @date : 2018/5/2 16:05
 */

@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //设置所有字段都参加序列化
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        //设置格式化的格式
        // NON_FINAL : [Object,Object.toJson格式] 按对象反序列化
        //JAVA_LANG_OBJECT : Object.toJson 格式 按字段反序列化
        objectMapper.enableDefaultTyping(DefaultTyping.NON_FINAL);

        //格式化时间 yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //忽略空Bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);

        //忽略在json字符串中存在,但java字段中不存在的情况,防止错误
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static <T> String obj2String(T obj){
        if (obj == null){
            return null;
        }

        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        }catch (Exception e){
            log.warn("对象转换异常:\n",e);
            return null;
        }
    }


    public static <T> String obj2StringPretty(T obj){
        if (obj == null){
            return null;
        }

        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        }catch (Exception e){
            log.warn("对象转换异常:\n",e);
            return null;
        }
    }


    public static <T> T string2Obj(String str,Class<T> clazz){
        if (StringUtils.isEmpty(str) || clazz == null){
            return null;
        }

        try {
            return clazz.equals(String.class) ? (T) str:objectMapper.readValue(str,clazz);
        }catch (Exception e){
            log.warn("对象转换异常:\n",e);
            return null;
        }
    }

    public static <T> T string2Obj(String str,TypeReference<T> typeReference){
        if (StringUtils.isEmpty(str) || typeReference == null){
            return null;
        }

        try {
            return typeReference.getType().equals(String.class)? (T) str:objectMapper.readValue(str,typeReference);
        }catch (Exception e){
            log.warn("对象转换异常:\n",e);
            return null;
        }
    }


    public static <T> T string2Obj(String str,Class<?> collectionClass,Class<?>...elementClasses){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);

        try {
            return objectMapper.readValue(str,javaType);
        } catch (IOException e) {
            log.warn("对象转换异常\n",e);
            return null;
        }

    }

}
