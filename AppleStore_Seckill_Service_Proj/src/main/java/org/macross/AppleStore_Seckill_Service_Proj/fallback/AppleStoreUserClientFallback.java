package org.macross.AppleStore_Seckill_Service_Proj.fallback;

import lombok.extern.slf4j.Slf4j;
import org.macross.AppleStore_Common_Config.model.request.UpdateAccountRequest;
import org.macross.AppleStore_Common_Config.utils.JsonData;
import org.macross.AppleStore_Seckill_Service_Proj.client.AppleStoreUserClient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppleStoreUserClientFallback implements AppleStoreUserClient {

    @Override
    public JsonData findUserInfoByUserId(Integer user_id) {
        log.error("findUserInfoByUserId API 请求异常");
        return null;
    }

    @Override
    public JsonData updateUserAccount(UpdateAccountRequest updateAccountRequest) {
        log.error("updateUserAccount API 请求异常");
        return null;
    }
}
