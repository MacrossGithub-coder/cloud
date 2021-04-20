package org.macross.AppleStore_Seckill_Service_Proj.service;

/**
 * @Author: jiancheng.zhang
 * @Date: 2021/4/20 17:37
 */
public interface SeckillResultService {
    int getSeckillResult(Integer commodityId, Integer userId, String outTradeNo);

    boolean resetDatabase() throws Exception;
}
