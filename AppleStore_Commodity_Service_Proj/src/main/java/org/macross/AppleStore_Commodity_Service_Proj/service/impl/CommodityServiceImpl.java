package org.macross.AppleStore_Commodity_Service_Proj.service.impl;

import org.macross.AppleStore_Commodity_Service_Proj.config.CacheKeyManager;
import org.macross.AppleStore_Commodity_Service_Proj.mapper.CommodityMapper;
import org.macross.AppleStore_Commodity_Service_Proj.service.CommodityService;
import org.macross.AppleStore_Commodity_Service_Proj.utils.BaseCache;
import org.macross.AppleStore_Common_Config.model.entity.Commodity;
import org.macross.AppleStore_Common_Config.model.entity.CommodityCategory;
import org.macross.AppleStore_Common_Config.model.entity.HomeBanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommodityServiceImpl implements CommodityService {

    @Autowired
    BaseCache baseCache;

    @Autowired
    CommodityMapper commodityMapper;

    @Override
    public List<HomeBanner> homeBanner() {

        try {
            Object Cache = baseCache.getTenMinuteCache().get(CacheKeyManager.INDEX_BANNER_LIST_KEY, () ->
            {
                return commodityMapper.homeBanner();
            });
            if (Cache instanceof List){
                return (List<HomeBanner>)Cache;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public List<CommodityCategory> homeCommodity() {

        try {
            Object Cache = baseCache.getOneHourCache().get(CacheKeyManager.INDEX_COMMODITY_LIST_KEY, () ->
            {
                return commodityMapper.homeCommodity();
            });
            if (Cache instanceof List){
                return (List<CommodityCategory>)Cache;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Commodity findCommodityDetail(Integer commodityId) {

        String INDEX_COMMODITY_DETAIL_KEY = String.format(CacheKeyManager.INDEX_COMMODITY_DETAIL_KEY, commodityId);

        try {

            Object Cache =  baseCache.getOneHourCache().get(INDEX_COMMODITY_DETAIL_KEY,() ->
            {
                return commodityMapper.findCommodityDetail(commodityId);
            });
            if (Cache instanceof Commodity){
                return (Commodity)Cache;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public List<Commodity> seckillCommodityList() {
        List<Commodity> commodityList =  commodityMapper.findAllSeckillCommodity();
        commodityList.forEach(e->e.setDescribe("[秒杀价]"+e.getDescribe()));
        return commodityList;
    }

    @Override
    public Commodity findSeckillCommodityDetail(Integer commodityId) {
        Commodity seckillCommodityDetail = commodityMapper.findSeckillCommodityDetail(commodityId);
        seckillCommodityDetail.setDescribe("[秒杀价]"+seckillCommodityDetail.getDescribe());
        return seckillCommodityDetail;
    }

}
