package org.macross.AppleStore_Order_Service_proj.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.macross.AppleStore_Common_Config.exception.RunTimeException;
import org.macross.AppleStore_Common_Config.model.entity.Commodity;
import org.macross.AppleStore_Common_Config.model.entity.CommodityOrder;
import org.macross.AppleStore_Common_Config.model.entity.User;
import org.macross.AppleStore_Common_Config.model.request.UpdateAccountRequest;
import org.macross.AppleStore_Order_Service_proj.client.AppleStoreCommodityClient;
import org.macross.AppleStore_Order_Service_proj.client.AppleStoreUserClient;
import org.macross.AppleStore_Order_Service_proj.mapper.CommodityOrderMapper;
import org.macross.AppleStore_Order_Service_proj.service.CommodityOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CommodityOrderServiceImpl implements CommodityOrderService {

    @Autowired
    AppleStoreUserClient appleStoreUserClient;

    @Autowired
    AppleStoreCommodityClient appleStoreCommodityClient;

    @Autowired
    CommodityOrderMapper commodityOrderMapper;

    /**
     * 返回JsonData
     * code: -1 系统异常
     * code: -2 数据库用户信息有误
     * code: -3 数据库商品信息有误
     * code: -4 余额不足
     * code: 0 正常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int commodityOrder(Integer userId, Integer commodityId) {

        ObjectMapper objectMapper = new ObjectMapper();
        //查询商品信息
        Commodity commodityDetail = objectMapper.convertValue(appleStoreCommodityClient.findCommodityDetail(commodityId).getData(), Commodity.class);
        if (commodityDetail == null) {
            throw new RunTimeException(-3, "数据库商品信息有误，请联系开发者");
        }
        for (; ; ) {
            //查询用户信息
            User userInfo = objectMapper.convertValue(appleStoreUserClient.findUserInfoByUserId(userId).getData(), User.class);

            if (userInfo == null) {
                throw new RunTimeException(-2, "数据库用户信息有误，请联系开发者");
            }

            //修改账户余额
            BigDecimal orgAccount = userInfo.getAccount();
            BigDecimal finalAccount = orgAccount.subtract(commodityDetail.getPrice());

            if (finalAccount.signum() == -1) {
                throw new RunTimeException(-4, "余额不足");
            }

            CommodityOrder commodityOrder = new CommodityOrder();
            commodityOrder.setOutTradeNo(UUID.randomUUID().toString());
            commodityOrder.setUserId(userInfo.getId());
            commodityOrder.setState(1);
            commodityOrder.setTotalFee(commodityDetail.getPrice());
            commodityOrder.setCommodityId(commodityDetail.getId());
            commodityOrder.setCommodityDescribe(commodityDetail.getDescribe());
            commodityOrder.setCommodityImg(commodityDetail.getHomeImg());
            commodityOrder.setAddress(userInfo.getAddress());
            commodityOrder.setIfSeckill(0);
            commodityOrder.setCreateTime(new Date());

            //插入Order表
            int result1 = commodityOrderMapper.commodityOrder(commodityOrder);

            //修改账户余额
            Integer result2 = appleStoreUserClient.updateUserAccount(new UpdateAccountRequest(userInfo.getId(), finalAccount, orgAccount)).getCode();

            if (result1 > 0 && result2 == 0) {
                return 1;
            }
        }
    }


    @Override
    public List<CommodityOrder> findUserOrder(Integer userId) {
        log.info("Receive Request findUserOrder userId={}", userId.toString());
        List<CommodityOrder> userCommodityOrder = commodityOrderMapper.findUserOrder(userId);
        return userCommodityOrder;
    }

    @Override
    public int commodityOrderByFeign(CommodityOrder commodityOrder) {
        log.info("Receive Request commodityOrder param [{}]", commodityOrder.toString());
        int result = commodityOrderMapper.seckillCommodityOrder(commodityOrder);
        return result > 0 ? commodityOrder.getId() : 0;
    }
}
