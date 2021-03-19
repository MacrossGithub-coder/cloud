package org.macross.AppleStore_Order_Service_proj.fallback;

import lombok.extern.slf4j.Slf4j;
import org.macross.AppleStore_Common_Config.utils.JsonData;
import org.macross.AppleStore_Order_Service_proj.client.AppleStoreCommodityClient;
import org.springframework.stereotype.Component;

/**
 * @Author: jiancheng.zhang
 * @Date: 2021/1/7 10:27
 */
@Component
@Slf4j
public class AppleStoreCommodityClientFallback implements AppleStoreCommodityClient {

    @Override
    public JsonData findCommodityDetail(Integer commodityId) {
        log.error("findCommodityDetail API 请求异常");
        return null;
    }
}
