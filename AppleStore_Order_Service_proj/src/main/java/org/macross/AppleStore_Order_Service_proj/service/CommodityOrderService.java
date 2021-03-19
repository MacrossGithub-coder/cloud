package org.macross.AppleStore_Order_Service_proj.service;


import org.macross.AppleStore_Common_Config.model.entity.CommodityOrder;

import java.util.List;

public interface CommodityOrderService {
    int commodityOrder(Integer userId, Integer commodityId);

    List<CommodityOrder> findUserOrder(Integer userId);

    int commodityOrderByFeign(CommodityOrder commodityOrder);
}
