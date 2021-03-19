package org.macross.AppleStore_Seckill_Service_Proj.client;

import org.macross.AppleStore_Common_Config.model.entity.CommodityOrder;
import org.macross.AppleStore_Common_Config.utils.JsonData;
import org.macross.AppleStore_Seckill_Service_Proj.fallback.AppleStoreCommodityOrderFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author: jiancheng.zhang
 * @Date: 2021/1/15 11:38
 */
@FeignClient(name = "AppleStore-Order-Service",fallback = AppleStoreCommodityOrderFallback.class)
public interface AppleStoreCommodityOrderClient {

    @RequestMapping(value = "/api/v1/pri/order/commodity_order_by_feign",method = RequestMethod.POST)
    JsonData commodityOrderByFeign(@RequestBody CommodityOrder commodityOrder);
}