package org.macross.AppleStore_Seckill_Service_Proj.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.macross.AppleStore_Common_Config.model.entity.CommoditySeckill;
import org.macross.AppleStore_Common_Config.utils.JsonData;
import org.macross.AppleStore_Seckill_Service_Proj.rabbitmq.SeckillMessage;


/**
 * @Author: jiancheng.zhang
 * @Date: 2021/1/15 10:23
 */

public interface CommoditySeckillService {
    JsonData doCommoditySeckill(Integer commodityId, Integer userId) throws JsonProcessingException;

    void doSeckillService(SeckillMessage seckillMessage, CommoditySeckill commoditySeckill) throws JsonProcessingException;
}
