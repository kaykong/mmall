package top.kongk.mmall.common;

import org.junit.Test;

public class TokenCacheTest {
    @Test
    public void put() throws Exception {
        String key = "kk";
        String value = "123";
        TokenCache.put(key, value);
    }


    @Test
    public void get() throws Exception {
        String key = "kk";

        System.out.println(TokenCache.get(key));
    }

}