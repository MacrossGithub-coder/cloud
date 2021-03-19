package org.macross.AppleStore_Seckill_Service_Proj.mapper;

import org.apache.ibatis.annotations.Param;
import org.macross.AppleStore_Common_Config.model.entity.CommodityOrder;
import org.macross.AppleStore_Common_Config.model.entity.CommoditySeckill;
import org.macross.AppleStore_Common_Config.model.entity.SeckillOrder;


public interface CommoditySeckillMapper {
    SeckillOrder findSeckillOrder(@Param("user_id") Integer userId, @Param("commodity_id") Integer commodityId);

    CommoditySeckill findStockByCommodityId(@Param("commodity_id") Integer commodityId);

    int reduceStock(@Param("commodity_id") Integer commodityId);

    void commoditySeckillOrder(SeckillOrder seckillOrder);

    CommodityOrder findSeckillResult(@Param("out_trade_no") String outTradeNo);

    void seckillCommodityOrderWithFail(CommodityOrder commodityOrder);
}
