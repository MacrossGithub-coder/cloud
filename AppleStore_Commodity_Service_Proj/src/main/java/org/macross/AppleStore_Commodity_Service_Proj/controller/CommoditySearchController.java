package org.macross.AppleStore_Commodity_Service_Proj.controller;

import lombok.extern.slf4j.Slf4j;
import org.macross.AppleStore_Commodity_Service_Proj.service.ElasticSearchService;
import org.macross.AppleStore_Common_Config.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/pub/es")
@Slf4j
public class CommoditySearchController {

    @Autowired
    ElasticSearchService elasticSearchService;

    @GetMapping("update_data")
    public JsonData updateData() throws IOException {
        log.info("Receive query updateData ");
        boolean result = elasticSearchService.updateData();
        return !result ? JsonData.buildSuccess("Update true"):JsonData.buildError("Update False");
    }

    @RequestMapping("search_commodity")
    public JsonData searchCommodity(@RequestParam("keyword")String keyword) throws IOException {
        log.info("Receive query searchCommodity param [{}]",keyword);
        List<Map<String,Object>> resultList =  elasticSearchService.searchCommodity(keyword);
        return resultList.size()>0 ? JsonData.buildSuccess(resultList):JsonData.buildError("未查询到结果");
    }
}
