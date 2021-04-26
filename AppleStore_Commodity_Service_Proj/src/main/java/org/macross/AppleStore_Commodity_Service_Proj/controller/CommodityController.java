package org.macross.AppleStore_Commodity_Service_Proj.controller;

import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.macross.AppleStore_Commodity_Service_Proj.service.CommodityService;
import org.macross.AppleStore_Common_Config.model.entity.Commodity;
import org.macross.AppleStore_Common_Config.model.entity.CommodityCategory;
import org.macross.AppleStore_Common_Config.model.entity.HomeBanner;
import org.macross.AppleStore_Common_Config.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/pub/list")
@Slf4j
public class CommodityController {

    @Autowired
    CommodityService commodityService;

    @SentinelResource(value = "home_banner",blockHandler = "exceptionHandler",entryType = EntryType.IN)
    @RequestMapping(value = "home_banner", method = RequestMethod.GET)
    public JsonData homeBanner() {
            log.info("Receive query home_banner");
            List<HomeBanner> homeBannerList = commodityService.homeBanner();
            return homeBannerList != null ? JsonData.buildSuccess(homeBannerList) : JsonData.buildError("查询失败");
    }


    @SentinelResource(value = "home_Commodity",blockHandler = "exceptionHandler",entryType = EntryType.IN)
    @RequestMapping(value = "home_Commodity", method = RequestMethod.GET)
    public JsonData homeCommodity() {
            log.info("Receive query home_Commodity");
            List<CommodityCategory> commodityCategoryList = commodityService.homeCommodity();
            return commodityCategoryList != null ? JsonData.buildSuccess(commodityCategoryList) : JsonData.buildError("查询失败");
    }

    @RequestMapping(value = "find_commodity_detail", method = RequestMethod.GET)
    @SentinelResource(value = "find_commodity_detail",blockHandler = "exceptionHandler",entryType = EntryType.IN)
    public JsonData findCommodityDetail(@RequestParam("commodity_id") Integer commodityId) {
        log.info("Receive query findCommodityDetail param [{}]",commodityId);
        Commodity commodity = commodityService.findCommodityDetail(commodityId);
        return commodity != null ? JsonData.buildSuccess(commodity) : JsonData.buildError("查询失败");
    }

    @RequestMapping(value = "seckill_commodity_list",method = RequestMethod.GET)
    @SentinelResource(value = "seckill_commodity_list",blockHandler = "exceptionHandler",entryType = EntryType.IN)
    public JsonData seckillCommodityList(){
        log.info("Receive query seckill_commodity_list");
        List<Commodity> commodityList = commodityService.seckillCommodityList();
        return commodityList != null ? JsonData.buildSuccess(commodityList) : JsonData.buildError("查询失败");
    }

    @RequestMapping(value = "find_seckill_commodity_detail", method = RequestMethod.GET)
    @SentinelResource(value = "find_seckill_commodity_detail",blockHandler = "exceptionHandler",entryType = EntryType.IN)
    public JsonData findSeckillCommodityDetail(@RequestParam("commodity_id") Integer commodityId) {
        log.info("Receive query findSeckillCommodityDetail param [{}]",commodityId);
        Commodity commodity = commodityService.findSeckillCommodityDetail(commodityId);
        return commodity != null ? JsonData.buildSuccess(commodity) : JsonData.buildError("查询失败");
    }

    @RequestMapping(value = "upload_image")
    public JsonData uploadImage(HttpServletRequest request) throws IOException {
        log.info("Receive uploadImage request");
        List<String> result = commodityService.uploadImage(request);
        return new JsonData(0,result,"UploadImage Success");
    }

    public JsonData exceptionHandler(BlockException e){
//        log.error("当前访问用户过多，接口已做限流处理");
        return JsonData.buildError("当前访问用户过多，接口已做限流处理，请稍后重试");
    }
}
