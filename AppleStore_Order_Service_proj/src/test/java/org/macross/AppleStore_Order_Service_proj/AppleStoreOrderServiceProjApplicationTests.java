package org.macross.AppleStore_Order_Service_proj;

import org.junit.jupiter.api.Test;
import org.macross.AppleStore_Order_Service_proj.client.AppleStoreCommodityClient;
import org.macross.AppleStore_Order_Service_proj.client.AppleStoreUserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AppleStoreOrderServiceProjApplicationTests {

    @Autowired
    AppleStoreUserClient appleStoreUserClient;

    @Autowired
    AppleStoreCommodityClient appleStoreCommodityClient;


	@Test
	void contextLoads() {
	}

    @Test
    void testFeign(){
//        LinkedHashMap userInfo = (LinkedHashMap)appleStoreUserClient.findUserInfoByUserId(29).getData();
//        LinkedHashMap data = (LinkedHashMap)appleStoreCommodityClient.findCommodityDetail(1).getData();
//        Integer result =  appleStoreUserClient.updateUserAccount(new UpdateAccountRequest(31, 11,9991)).getCode();
//        System.out.println(result);
//        ObjectMapper objectMapper = new ObjectMapper();
//        User user = objectMapper.convertValue(userInfo, User.class);
//        System.out.println(user);
//        Commodity commodity = objectMapper.convertValue(data, Commodity.class);
//        System.out.println(commodity);

    }


}
