package org.macross.AppleStore_Order_Service_proj.client;

import org.macross.AppleStore_Common_Config.model.request.UpdateAccountRequest;
import org.macross.AppleStore_Common_Config.utils.JsonData;
import org.macross.AppleStore_Order_Service_proj.fallback.AppleStoreUserClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: jiancheng.zhang
 * @Date: 2021/1/7 10:04
 */
@FeignClient(name = "AppleStore-User-Service",fallback = AppleStoreUserClientFallback.class)
public interface AppleStoreUserClient{

    @RequestMapping(value = "api/v1/pri/user/find_by_user_id",method = RequestMethod.POST)
    JsonData findUserInfoByUserId(@RequestParam("user_id")Integer userId);

    @RequestMapping(value = "api/v1/pri/user/update_user_account",method = RequestMethod.POST)
    JsonData updateUserAccount(@RequestBody UpdateAccountRequest updateAccountRequest);
}
