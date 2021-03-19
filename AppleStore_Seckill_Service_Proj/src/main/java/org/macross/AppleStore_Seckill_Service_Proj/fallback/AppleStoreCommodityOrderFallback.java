package org.macross.AppleStore_Seckill_Service_Proj.fallback;

import lombok.extern.slf4j.Slf4j;
import org.macross.AppleStore_Common_Config.model.entity.CommodityOrder;
import org.macross.AppleStore_Common_Config.utils.JsonData;
import org.macross.AppleStore_Seckill_Service_Proj.client.AppleStoreCommodityOrderClient;
import org.springframework.stereotype.Component;


/**
 * @Author: jiancheng.zhang
 * @Date: 2021/1/15 11:41
 */
@Component
@Slf4j
public class AppleStoreCommodityOrderFallback implements AppleStoreCommodityOrderClient {
    @Override
    public JsonData commodityOrderByFeign(CommodityOrder commodityOrder) {
        log.error("commodityOrderByFeign API 请求异常");
        return null;
    }
}
