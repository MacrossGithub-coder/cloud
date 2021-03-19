package org.macross.AppleStore_Commodity_Service_Proj.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.macross.AppleStore_Commodity_Service_Proj.mapper.CommodityMapper;
import org.macross.AppleStore_Commodity_Service_Proj.service.ElasticSearchService;
import org.macross.AppleStore_Common_Config.model.entity.Commodity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ElasticServiceImpl implements ElasticSearchService {

    @Autowired
    CommodityMapper commodityMapper;

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Override
    public boolean updateData() throws IOException {

        //获取数据库中的商品数据
        List<Commodity> allCommodity = commodityMapper.findAllCommodity();
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(new TimeValue(10, TimeUnit.SECONDS));
        ObjectMapper objectMapper = new ObjectMapper();
        for (Commodity data : allCommodity) {
            bulkRequest.add(new IndexRequest("apple-mall")
                    .source(objectMapper.writeValueAsString(data), XContentType.JSON));
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return bulk.hasFailures();

    }

    @Override
    public List<Map<String, Object>> searchCommodity(String keyword) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.timeout(new TimeValue(5, TimeUnit.SECONDS));
        ;
//        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("describe", keyword));
//        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("describe", keyword);
        char[] c = keyword.toCharArray();
        int count = 0;
        for (int i = 0; i < c.length; i++) {
            String len = Integer.toBinaryString(c[i]);
            if (len.length() > 8)
                count++;
        }
        WildcardQueryBuilder wildcardQueryBuilder = null;
        if (count > 1) {
            wildcardQueryBuilder = QueryBuilders.wildcardQuery("describe.keyword", "*" + keyword + "*");
        } else {
            wildcardQueryBuilder = QueryBuilders.wildcardQuery("describe", "*" + keyword + "*");
        }
        searchSourceBuilder.query(wildcardQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            list.add(hit.getSourceAsMap());
        }
        return list;
    }


}
