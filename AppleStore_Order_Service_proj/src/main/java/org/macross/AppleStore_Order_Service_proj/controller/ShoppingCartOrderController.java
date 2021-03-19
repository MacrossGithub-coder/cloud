package org.macross.AppleStore_Order_Service_proj.controller;


import lombok.extern.slf4j.Slf4j;
import org.macross.AppleStore_Common_Config.model.entity.ShoppingCartOrder;
import org.macross.AppleStore_Common_Config.model.request.CommonRequest;
import org.macross.AppleStore_Common_Config.utils.JsonData;
import org.macross.AppleStore_Order_Service_proj.service.ShoppingCartOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api/v1/pri/cart")
@Slf4j
public class ShoppingCartOrderController {

    @Autowired
    ShoppingCartOrderService shoppingCartOrderService;

    @RequestMapping(value = "add_to_cart",method = RequestMethod.POST)
    public JsonData addToCart(@RequestBody CommonRequest commonRequest, HttpServletRequest request) {
        log.info("Receive Request addToCart param [{}]", commonRequest.getCommodity_id());
        Integer userId = Integer.parseInt(request.getHeader("user_id"));
        int result = shoppingCartOrderService.addToCart(userId, commonRequest.getCommodity_id());

        return result > 0 ? JsonData.buildSuccess("加入购物车成功") : JsonData.buildError("加入购物车失败");
    }

    @RequestMapping(value = "empty_cart",method = RequestMethod.POST)
    public JsonData emptyCart(HttpServletRequest request) {
        log.info("Receive Request emptyCart");
        Integer userId = Integer.parseInt(request.getHeader("user_id"));
        int result = shoppingCartOrderService.emptyCart(userId);

        return result > 0 ? JsonData.buildSuccess("清空购物车成功") : JsonData.buildError("清空购物车失败");
    }

    @RequestMapping(value = "find_user_cart_info",method = RequestMethod.POST)
    public JsonData findUserCartInfo(HttpServletRequest request) {
        log.info("Receive Request findUserCartInfo");
        Integer userId = Integer.parseInt(request.getHeader("user_id"));
        List<ShoppingCartOrder> shoppingCartOrderList = shoppingCartOrderService.findUserCartInfo(userId);

        return shoppingCartOrderList.size() > 0 ? JsonData.buildSuccess(shoppingCartOrderList) : JsonData.buildError("该用户购物车为空");
    }
}
