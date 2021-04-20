package org.macross.AppleStore_Seckill_Service_Proj.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.macross.AppleStore_Common_Config.model.entity.CommodityOrder;
import org.macross.AppleStore_Common_Config.model.entity.SeckillOrder;
import org.macross.AppleStore_Seckill_Service_Proj.client.AppleStoreCommodityClient;
import org.macross.AppleStore_Seckill_Service_Proj.client.AppleStoreUserClient;
import org.macross.AppleStore_Seckill_Service_Proj.config.SeckillConstant;
import org.macross.AppleStore_Seckill_Service_Proj.mapper.CommoditySeckillMapper;
import org.macross.AppleStore_Seckill_Service_Proj.service.SeckillResultService;
import org.macross.AppleStore_Seckill_Service_Proj.utils.YmlUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * @Author: jiancheng.zhang
 * @Date: 2021/4/20 17:38
 */
@Service
@Slf4j
public class SeckillResultServiceImpl implements SeckillResultService {

    @Autowired
    @Qualifier("redisTemplateMasterSeckill")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    @Qualifier("redisTemplateSlave2Seckill")
    private RedisTemplate<String, Object> redisSlave2Template;

    @Autowired
    private CommoditySeckillMapper commoditySeckillMapper;

    @Autowired
    AppleStoreUserClient appleStoreUserClient;

    @Autowired
    AppleStoreCommodityClient appleStoreCommodityClient;

    @Autowired
    public RabbitTemplate rabbitTemplate;

    private static final String COMMODITY_OVER = "commodity_over:";
    /**
     * @param commodityId
     * @param userId
     * @param outTradeNo
     * @return
     * -1 ：商品库存不足
     *  0 ：排队中，继续轮询
     * -2 : 用户余额不足
     * -3 : 秒杀过程中出现异常，从Redis缓存中取出相关信息，页面返回活动火爆
     * 秒杀成功，返回订单ID
     */
    @Override
    public int getSeckillResult(Integer commodityId, Integer userId, String outTradeNo) {

        SeckillOrder seckillOrder = commoditySeckillMapper.findSeckillOrder(userId, commodityId);
        if (seckillOrder != null) {
            return seckillOrder.getOrderId();
        }

        //查看用户是否余额不足或者系统异常
        CommodityOrder commodityOrder = commoditySeckillMapper.findSeckillResult(outTradeNo);
        if (!ObjectUtils.isEmpty(commodityOrder)) {
            if (SeckillConstant.INSUFFICIENT_BALANCE.equals(commodityOrder.getSeckillCode())) {
                log.error(commodityOrder.getFailMsg());
                return SeckillConstant.INSUFFICIENT_BALANCE;
            }
            if (SeckillConstant.EXCEPTION.equals(commodityOrder.getSeckillCode())) {
                log.error(commodityOrder.getFailMsg());
                return SeckillConstant.EXCEPTION;
            }
        }

        //查看商品是否被秒杀完
        Boolean isOver = redisSlave2Template.hasKey(COMMODITY_OVER + commodityId);
        if (isOver != null && isOver) {
            return SeckillConstant.OUT_OF_STOCK;
        }
        return SeckillConstant.IN_QUEUE;
    }

    @Override
    public boolean resetDatabase() throws Exception {
        //Reset redis database
        redisTemplate.opsForValue().set("21",50);

        //Truncate table & reset commodity stock
        Map<String, String> datasource = YmlUtils.getYmlByFileName("classpath:application-dev.yml", "spring", "datasource");
        Connection conn;
        if (datasource!=null){
            String url = datasource.get("spring.datasource.url");
            String username = datasource.get("spring.datasource.username");
            String password = datasource.get("spring.datasource.password");
            try {
                conn = DriverManager.getConnection(url,username,password);
            } catch (SQLException throwable) {
                throw new Exception(throwable.getMessage());
            }
        }else {
            return false;
        }
        ScriptRunner runner = new ScriptRunner(conn);
        runner.setErrorLogWriter(null);
        runner.setLogWriter(null);
        runner.runScript(Resources.getResourceAsReader("scripts/seckill.sql"));
        conn.close();
        log.info("=======Reset Database Success=======");
        return true;
    }
}
