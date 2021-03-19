package org.macross.AppleStore_Seckill_Service_Proj.lua;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;

/**
 * @Author: jiancheng.zhang
 * @Date: 2021/2/24 15:12
 */
@Slf4j
@Component
public class DistributedLimit {

    @Autowired
    @Qualifier("redisTemplateMasterLimit")
    private StringRedisTemplate redisTemplate;

    @Autowired
    RedisScript<Long> limitScript;

    public Boolean distributedLimit(String key, String limit) {
        Long result = 0L;
        try {
            result = redisTemplate.execute(limitScript, Collections.singletonList(key),
                    limit);
        } catch (Exception e) {
            log.error("error", e);
        }
        if (Objects.isNull(result)) return false;
        return result != 0L;
    }

}