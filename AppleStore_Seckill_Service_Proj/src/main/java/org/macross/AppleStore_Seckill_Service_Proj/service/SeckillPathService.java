package org.macross.AppleStore_Seckill_Service_Proj.service;

/**
 * @Author: jiancheng.zhang
 * @Date: 2021/4/20 17:29
 */
public interface SeckillPathService {
    String createSeckillPath(Integer commodityId, Integer userId);

    boolean confirmPathValid(String key,String path);
}
