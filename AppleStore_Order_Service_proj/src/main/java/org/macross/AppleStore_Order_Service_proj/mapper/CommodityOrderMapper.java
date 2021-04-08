package org.macross.AppleStore_Order_Service_proj.mapper;

import org.apache.ibatis.annotations.Param;
import org.macross.AppleStore_Common_Config.model.entity.CommodityOrder;

import java.util.List;

public interface CommodityOrderMapper {

    int commodityOrder(CommodityOrder commodityOrder);

    int seckillCommodityOrder(CommodityOrder commodityOrder);

    List<CommodityOrder> findUserOrder(@Param("userId") Integer userId);
}
