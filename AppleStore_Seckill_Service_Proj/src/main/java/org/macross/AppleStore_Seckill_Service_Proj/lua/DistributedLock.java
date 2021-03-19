package org.macross.AppleStore_Seckill_Service_Proj.lua;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @Author: jiancheng.zhang
 * @Date: 2021/2/25 11:15
 */
@Slf4j
@Component
public class DistributedLock {

    @Autowired
    @Qualifier("redisTemplateMasterLimit")
    private StringRedisTemplate redisTemplate;

    @Autowired
    RedisScript<Boolean> lockScript;

    @Autowired
    RedisScript<Long> unlockScript;

    public Boolean distributedLock(String key, String uuid, String secondsToLock) {
        Boolean locked = false;
        try {
            String millSeconds = String.valueOf(Integer.parseInt(secondsToLock) * 1000);
            locked =redisTemplate.execute(lockScript, Collections.singletonList(key), uuid, millSeconds);
            log.info("distributedLock.key{}: - uuid:{}: - timeToLock:{} - locked:{} - millSeconds:{}",
                    key, uuid, secondsToLock, locked, millSeconds);
        } catch (Exception e) {
            log.error("error", e);
        }
        return locked;
    }

    public void distributedUnlock(String key, String uuid) {
        Long unlocked = redisTemplate.execute(unlockScript, Collections.singletonList(key),
                uuid);
        log.info("distributedLock.key{}: - uuid:{}: - unlocked:{}", key, uuid, unlocked);

    }

}
