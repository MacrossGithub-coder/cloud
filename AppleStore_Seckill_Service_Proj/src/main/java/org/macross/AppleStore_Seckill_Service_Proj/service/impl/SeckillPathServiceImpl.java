package org.macross.AppleStore_Seckill_Service_Proj.service.impl;

import org.macross.AppleStore_Seckill_Service_Proj.service.SeckillPathService;
import org.macross.AppleStore_Seckill_Service_Proj.utils.CommonsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: jiancheng.zhang
 * @Date: 2021/4/20 17:29
 */
@Service
public class SeckillPathServiceImpl implements SeckillPathService {

    @Autowired
    @Qualifier("redisTemplateMasterSeckill")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    @Qualifier("redisTemplateSlave1Seckill")
    private RedisTemplate<String, Object> redisSlave1Template;

    @Autowired
    @Qualifier("redisTemplateSlave2Seckill")
    private RedisTemplate<String, Object> redisSlave2Template;

    @Override
    public String createSeckillPath(Integer commodityId, Integer userId) {
        String str = UUID.randomUUID().toString();

        //Redis:{key:"path:userId:commodityId,Value:str}
        String key = "path:" + userId + ":" + commodityId;
        String value = CommonsUtils.MD5(str);
        if (value == null) return null;
        //redisTemplate.opsForValue().set(key, value, 60 * 10L, TimeUnit.SECONDS);
        //测试使用的SeckillPath,ttl=7Days
        redisTemplate.opsForValue().set(key, value, 60 * 60 *24 *7L, TimeUnit.SECONDS);
        return str;
    }

    @Override
    public boolean confirmPathValid(String key, String path) {

        String value = (String) redisSlave2Template.opsForValue().get(key);
        if (value == null) return false;
        return value.equals(CommonsUtils.MD5(path));
    }
}
