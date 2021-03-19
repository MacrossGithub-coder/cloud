package org.macross.AppleStore_Order_Service_proj.controller;

import lombok.extern.slf4j.Slf4j;
import org.macross.AppleStore_Common_Config.model.entity.CommodityOrder;
import org.macross.AppleStore_Common_Config.model.request.CommonRequest;
import org.macross.AppleStore_Common_Config.utils.JsonData;
import org.macross.AppleStore_Order_Service_proj.service.CommodityOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api/v1/pri/order")
@Slf4j
public class CommodityOrderController {

    @Autowired
    CommodityOrderService commodityOrderService;

    /**
     * @param commonRequest
     * @param request
     * @return
     * 返回JsonData
     * code: -1 数据库用户信息有误
     * code: -2 数据库商品信息有误
     * code: -3 余额不足
     * code: 0 正常
     */
    @RequestMapping(value = "commodity_order",method = RequestMethod.POST)
    public JsonData commodityOrder(@RequestBody CommonRequest commonRequest, HttpServletRequest request)  {
        log.info("Receive Request commodityOrder param [{}]",commonRequest.getCommodity_id());
        Integer userId = Integer.parseInt(request.getHeader("user_id"));
        int result = commodityOrderService.commodityOrder(userId,commonRequest.getCommodity_id());
        return result > 0 ? JsonData.buildSuccess("下单成功"):JsonData.buildError("下单失败");
    }

    @RequestMapping(value = "commodity_order_by_feign",method = RequestMethod.POST)
    public JsonData commodityOrderByFeign(@RequestBody CommodityOrder commodityOrder)  {
        log.info("Receive Request commodityOrder param [{}]",commodityOrder.toString());
        int orderId = commodityOrderService.commodityOrderByFeign(commodityOrder);
        return orderId > 0 ? JsonData.buildSuccess(orderId):JsonData.buildError("下单失败");
    }

    @RequestMapping(value = "find_user_order",method = RequestMethod.POST)
    public JsonData findUserOrder(HttpServletRequest request){
        Integer userId = Integer.parseInt(request.getHeader("user_id"));
        log.info("Receive Request findUserOrder userId={}",userId.toString());
        List<CommodityOrder> commodityOrderList = commodityOrderService.findUserOrder(userId);
        return commodityOrderList.size()>0 ? JsonData.buildSuccess(commodityOrderList):JsonData.buildError("该用户未下单");
    }
}
