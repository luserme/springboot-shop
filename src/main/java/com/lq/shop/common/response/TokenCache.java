package com.lq.shop.common.response;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j;

/**
 * @author : luqing
 * @date : 2018/4/19 16:57
 */
@Log4j
public class TokenCache {

    public static final String TOKEN_PREFIX = "token_";

    /**
     * LRU算法
     */
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
        .build(new CacheLoader<String, String>() {
            //默认的数据加载实现,当调用get取值的时候,如果key没有对应的值,就调用这个方法进行加载.
            @Override
            public String load(String s) throws Exception {
                return "null";
            }
        });

    public static void setKey(String key,String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            log.error("获取本地缓存失败",e);
        }
        return null;
    }

}
