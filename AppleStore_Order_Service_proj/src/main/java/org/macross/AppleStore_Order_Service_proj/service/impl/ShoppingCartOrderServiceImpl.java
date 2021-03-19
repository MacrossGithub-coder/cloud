package org.macross.AppleStore_Order_Service_proj.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.macross.AppleStore_Common_Config.exception.RunTimeException;
import org.macross.AppleStore_Common_Config.model.entity.Commodity;
import org.macross.AppleStore_Common_Config.model.entity.ShoppingCartOrder;
import org.macross.AppleStore_Common_Config.model.entity.User;
import org.macross.AppleStore_Order_Service_proj.client.AppleStoreCommodityClient;
import org.macross.AppleStore_Order_Service_proj.client.AppleStoreUserClient;
import org.macross.AppleStore_Order_Service_proj.mapper.ShoppingCartOrderMapper;
import org.macross.AppleStore_Order_Service_proj.service.CommodityOrderService;
import org.macross.AppleStore_Order_Service_proj.service.ShoppingCartOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ShoppingCartOrderServiceImpl implements ShoppingCartOrderService {

    @Autowired
    ShoppingCartOrderMapper shoppingCartOrderMapper;

    @Autowired
    AppleStoreUserClient appleStoreUserClient;

    @Autowired
    AppleStoreCommodityClient appleStoreCommodityClient;

    @Autowired
    CommodityOrderService commodityOrderService;


    @Override
    public int addToCart(Integer userId, Integer commodityId) {

        ObjectMapper objectMapper = new ObjectMapper();

        //查询用户信息
        User userInfo = objectMapper.convertValue(appleStoreUserClient.findUserInfoByUserId(userId).getData(), User.class);
        //查询商品信息
        Commodity commodityDetail = objectMapper.convertValue(appleStoreCommodityClient.findCommodityDetail(commodityId).getData(), Commodity.class);

        if (userInfo == null){
            throw new RunTimeException(-1,"数据库用户信息有误，请联系开发者");
        }
        if (commodityDetail == null){
            throw new RunTimeException(-2,"数据库商品信息有误，请联系开发者");
        }

        ShoppingCartOrder shoppingCartOrder = new ShoppingCartOrder();
        shoppingCartOrder.setOutTradeNo(UUID.randomUUID().toString());
        shoppingCartOrder.setUserId(userId);
        shoppingCartOrder.setState(0);//购物车state默认为0
        shoppingCartOrder.setTotalFee(commodityDetail.getPrice());
        shoppingCartOrder.setCommodityId(commodityDetail.getId());
        shoppingCartOrder.setCommodityDescribe(commodityDetail.getDescribe());
        shoppingCartOrder.setCommodityImg(commodityDetail.getHomeImg());
        shoppingCartOrder.setCreateTime(new Date());

        return shoppingCartOrderMapper.addToCart(shoppingCartOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int emptyCart(Integer userId) {

        ObjectMapper objectMapper = new ObjectMapper();

        //查询用户信息
        User userInfo = objectMapper.convertValue(appleStoreUserClient.findUserInfoByUserId(userId).getData(), User.class);

        if (userInfo == null){
            throw new RunTimeException(-1,"数据库用户信息有误，请联系开发者");
        }

        //查询用户购物车
        List<ShoppingCartOrder> userCartInfoList = shoppingCartOrderMapper.findUserCartInfo(userId);
        if (userCartInfoList.size() == 0){
            throw new RunTimeException(-2,"购物车为空");
        }

        //遍历用户购物车
        for (ShoppingCartOrder shoppingCartOrder:userCartInfoList){
            //下单
            commodityOrderService.commodityOrder(userId,shoppingCartOrder.getCommodityId());
        }
        //购物车订单->state=1

        return shoppingCartOrderMapper.updateOrderState(userId);
    }

    @Override
    public List<ShoppingCartOrder> findUserCartInfo(Integer userId) {
        return shoppingCartOrderMapper.findUserCartInfo(userId);
    }

}
