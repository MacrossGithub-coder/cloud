package org.macross.AppleStore_Commodity_Service_Proj.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ElasticSearchService {
    boolean updateData() throws IOException;

    List<Map<String, Object>> searchCommodity(String keyword) throws IOException;
}
