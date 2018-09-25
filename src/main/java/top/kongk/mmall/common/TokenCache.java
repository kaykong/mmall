package top.kongk.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
/**
 * 描述：用户token用到的guava缓存
 *
 * @Author: kk
 * @Date: 2018/9/22 16:08
 */

public class TokenCache {

    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);


    /**
     * 添加缓存的前缀
     */
    public static final String TOKEN_PREFIX = "token_username_";

    private static LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder()
            //初始化的容量
            .initialCapacity(1000)
            /*最大缓存的数目，当缓存中的数目逼近这个数值时，
             guava会使用LRU算法来清理（Least Recently Used 最近最少使用）*/
            .maximumSize(10000)
            //在被写入缓存30分钟后将会被回收掉
            .expireAfterWrite(30, TimeUnit.MINUTES)
            //这里使用默认的实现，当查不到该key对应的value时，返回null
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String s) throws Exception {
                    return null;
                }
            });

    /**
     * description: 把传来的 用户名 和 token 插入到缓存中
     *
     * @param key 用户名
     * @param value token
     * @return void
     */
    public static void put(String key, String value) {
        loadingCache.put(key + TokenCache.TOKEN_PREFIX, value);
    }

    /**
     * description: 根据用户名 获取对应的token
     *
     * @param key 用户名
     * @return java.lang.String
     */
    public static String get(String key) {
        String value = null;
        try {
            value = loadingCache.get(key + TokenCache.TOKEN_PREFIX);
        } catch (Exception e) {
            logger.error("method: get() : " + e);
        }
        return value;
    }

    /**
     * 描述：在确定该key对应的token不必再用后, 就清除掉
     *
     * @param key 用户名作为key
     * @return void
     */
    public static void invalidateToken(String key) {
        try {
            loadingCache.invalidate(key + TokenCache.TOKEN_PREFIX);
        } catch (Exception e) {
            logger.error("TokenCache.invalidateToken Execption",e);
        }
    }
}
