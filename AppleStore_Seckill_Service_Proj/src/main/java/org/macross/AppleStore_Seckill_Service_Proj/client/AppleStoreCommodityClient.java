package org.macross.AppleStore_Seckill_Service_Proj.client;

import org.macross.AppleStore_Common_Config.utils.JsonData;
import org.macross.AppleStore_Seckill_Service_Proj.fallback.AppleStoreCommodityClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: jiancheng.zhang
 * @Date: 2021/1/7 10:12
 */
@FeignClient(name = "AppleStore-Commodity-Service",fallback = AppleStoreCommodityClientFallback.class)
public interface AppleStoreCommodityClient {
    @RequestMapping(value = "/api/v1/pub/list/find_commodity_detail", method = RequestMethod.GET)
    JsonData findCommodityDetail(@RequestParam("commodity_id") Integer commodityId);
}
