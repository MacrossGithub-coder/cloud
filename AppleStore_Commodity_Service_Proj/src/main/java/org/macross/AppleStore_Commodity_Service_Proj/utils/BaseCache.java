package org.macross.AppleStore_Commodity_Service_Proj.utils;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class BaseCache {


    private Cache<String,Object> tenMinuteCache = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .maximumSize(100)
            .concurrencyLevel(5)
            .expireAfterWrite(6000, TimeUnit.SECONDS)
            .recordStats()
            .build();

    private Cache<String,Object> oneHourCache = CacheBuilder.newBuilder()
            .initialCapacity(30)
            .maximumSize(100)
            .concurrencyLevel(5)
            .expireAfterWrite(36000, TimeUnit.SECONDS)
            .recordStats()
            .build();


    public Cache<String, Object> getTenMinuteCache() {
        return tenMinuteCache;
    }

    public void setTenMinuteCache(Cache<String, Object> tenMinuteCache) {
        this.tenMinuteCache = tenMinuteCache;
    }

    public Cache<String, Object> getOneHourCache() {
        return oneHourCache;
    }

    public void setOneHourCache(Cache<String, Object> oneHourCache) {
        this.oneHourCache = oneHourCache;
    }
}
