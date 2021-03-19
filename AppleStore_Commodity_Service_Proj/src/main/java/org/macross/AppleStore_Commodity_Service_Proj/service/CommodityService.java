package org.macross.AppleStore_Commodity_Service_Proj.service;


import org.macross.AppleStore_Common_Config.model.entity.Commodity;
import org.macross.AppleStore_Common_Config.model.entity.CommodityCategory;
import org.macross.AppleStore_Common_Config.model.entity.HomeBanner;

import java.util.List;

public interface CommodityService {

    List<HomeBanner> homeBanner();

    List<CommodityCategory> homeCommodity();

    Commodity findCommodityDetail(Integer commodityId);

    List<Commodity> seckillCommodityList();

    Commodity findSeckillCommodityDetail(Integer commodityId);
}
