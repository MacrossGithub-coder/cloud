package org.macross.AppleStore_Commodity_Service_Proj.mapper;

import org.apache.ibatis.annotations.Param;
import org.macross.AppleStore_Common_Config.model.entity.Commodity;
import org.macross.AppleStore_Common_Config.model.entity.CommodityCategory;
import org.macross.AppleStore_Common_Config.model.entity.HomeBanner;

import java.util.List;

public interface CommodityMapper {

    List<HomeBanner> homeBanner();

    List<CommodityCategory> homeCommodity();

    List<Commodity> findAllCommodity();

    Commodity findCommodityDetail(@Param("commodity_id") Integer commodityId);

    List<Commodity> findAllSeckillCommodity();

    Commodity findSeckillCommodityDetail(@Param("commodity_id")Integer commodityId);
}
