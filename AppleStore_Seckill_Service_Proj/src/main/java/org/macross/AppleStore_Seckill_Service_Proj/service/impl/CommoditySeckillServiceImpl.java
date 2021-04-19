package org.macross.AppleStore_Seckill_Service_Proj.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.macross.AppleStore_Common_Config.model.entity.*;
import org.macross.AppleStore_Common_Config.model.request.UpdateAccountRequest;
import org.macross.AppleStore_Common_Config.utils.JsonData;
import org.macross.AppleStore_Seckill_Service_Proj.client.AppleStoreCommodityClient;
import org.macross.AppleStore_Seckill_Service_Proj.client.AppleStoreCommodityOrderClient;
import org.macross.AppleStore_Seckill_Service_Proj.client.AppleStoreUserClient;
import org.macross.AppleStore_Seckill_Service_Proj.config.SeckillConstant;
import org.macross.AppleStore_Seckill_Service_Proj.mapper.CommoditySeckillMapper;
import org.macross.AppleStore_Seckill_Service_Proj.rabbitmq.SeckillMessage;
import org.macross.AppleStore_Seckill_Service_Proj.service.CommoditySeckillService;
import org.macross.AppleStore_Seckill_Service_Proj.utils.CommonsUtils;
import org.macross.AppleStore_Seckill_Service_Proj.utils.YmlUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CommoditySeckillServiceImpl implements CommoditySeckillService {

    @Autowired
    @Qualifier("redisTemplateMasterSeckill")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    @Qualifier("redisTemplateSlave1Seckill")
    private RedisTemplate<String, Object> redisSlave1Template;

    @Autowired
    @Qualifier("redisTemplateSlave2Seckill")
    private RedisTemplate<String, Object> redisSlave2Template;

    @Autowired
    private CommoditySeckillMapper commoditySeckillMapper;

    @Autowired
    private AppleStoreCommodityOrderClient appleStoreCommodityOrderClient;

    @Autowired
    AppleStoreUserClient appleStoreUserClient;

    @Autowired
    AppleStoreCommodityClient appleStoreCommodityClient;

    @Autowired
    public RabbitTemplate rabbitTemplate;


    /**
     * -1 ：库存不足秒杀失败
     * -2 : 该用户存在重复下单行为
     * 0 ：排队中，继续轮询
     */
    private static final int SECKILL_ERROR = -1;
    private static final int REPEATED_SECKILL = -2;
    private static final int ADD_QUEUE = 0;

    private static final String COMMODITY_OVER = "commodity_over:";

    /**
     * @param commodityId
     * @param userId
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public JsonData doCommoditySeckill(Integer commodityId, Integer userId) throws JsonProcessingException {

        Integer orgStock = (Integer) redisSlave1Template.opsForValue().get(commodityId.toString());
        if (orgStock != null && orgStock <= 0) {
            return JsonData.buildError(SECKILL_ERROR, "库存不足秒杀失败");
        }

        //在Redis中进行库存预减
        Long stock = redisTemplate.opsForValue().decrement(commodityId.toString());
        if (stock != null && stock < 0) {
            return JsonData.buildError(SECKILL_ERROR, "库存不足秒杀失败");
        }

        //判断用户是否重复下单
        SeckillOrder seckillOrder = commoditySeckillMapper.findSeckillOrder(userId, commodityId);
        if (seckillOrder != null) {
            redisTemplate.opsForValue().increment(commodityId.toString());
            return JsonData.buildError(REPEATED_SECKILL, "请勿重复下单");
        }

        //正常请求，加入mq
        SeckillMessage seckillMessage = new SeckillMessage(userId, commodityId, UUID.randomUUID().toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(seckillMessage);
        //rabbitTemplate.convertAndSend("seckill_queue", "seckill", message);
        rabbitTemplate.convertAndSend("seckill_queue", message);
        return JsonData.buildSuccess(ADD_QUEUE, seckillMessage.getOutTradeNo());
    }


    /**
     * @param seckillMessage
     * @param commoditySeckill
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doSeckillService(SeckillMessage seckillMessage, CommoditySeckill commoditySeckill) throws JsonProcessingException {

        int state = commoditySeckillMapper.reduceStock(commoditySeckill.getCommodityId());
        if (state < 0) {
            //在缓存中进行标记
            redisTemplate.opsForValue().set(COMMODITY_OVER + commoditySeckill.getCommodityId().toString(),
                    true, 10 * 60L, TimeUnit.SECONDS);
            return;
        }
        int orderId = seckillCommodityOrder(seckillMessage, commoditySeckill);
        if (orderId <= 0) {
            return;
        }

        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setCommodityId(commoditySeckill.getCommodityId());
        seckillOrder.setOrderId(orderId);
        seckillOrder.setUserId(seckillMessage.getUserId());
        commoditySeckillMapper.commoditySeckillOrder(seckillOrder);
    }

    /**
     * -1 ：商品库存不足
     *  0 ：排队中，继续轮询
     * -2 : 用户余额不足
     * -3 : 秒杀过程中出现异常，从Redis缓存中取出相关信息，页面返回活动火爆
     * 秒杀成功，返回订单ID
     */
    @Override
    public int getSeckillResult(Integer commodityId, Integer userId, String outTradeNo) {

        SeckillOrder seckillOrder = commoditySeckillMapper.findSeckillOrder(userId, commodityId);
        if (seckillOrder != null) {
            return seckillOrder.getOrderId();
        }

        //查看用户是否余额不足或者系统异常
        CommodityOrder commodityOrder = commoditySeckillMapper.findSeckillResult(outTradeNo);
        if (!ObjectUtils.isEmpty(commodityOrder)) {
            if (SeckillConstant.INSUFFICIENT_BALANCE.equals(commodityOrder.getSeckillCode())) {
                log.error(commodityOrder.getFailMsg());
                return SeckillConstant.INSUFFICIENT_BALANCE;
            }
            if (SeckillConstant.EXCEPTION.equals(commodityOrder.getSeckillCode())) {
                log.error(commodityOrder.getFailMsg());
                return SeckillConstant.EXCEPTION;
            }
        }

        //查看商品是否被秒杀完
        Boolean isOver = redisSlave2Template.hasKey(COMMODITY_OVER + commodityId);
        if (isOver != null && isOver) {
            return SeckillConstant.OUT_OF_STOCK;
        }
        return SeckillConstant.IN_QUEUE;
    }

    @Override
    public String createSeckillPath(Integer commodityId, Integer userId) {
        String str = UUID.randomUUID().toString();

        //Redis:{key:"path:userId:commodityId,Value:str}
        String key = "path:" + userId + ":" + commodityId;
        String value = CommonsUtils.MD5(str);
        if (value == null) return null;
        //redisTemplate.opsForValue().set(key, value, 60 * 10L, TimeUnit.SECONDS);
        //测试使用的SeckillPath,ttl=7Days
        redisTemplate.opsForValue().set(key, value, 60 * 60 *24 *7L, TimeUnit.SECONDS);
        return str;
    }

    @Override
    public boolean confirmPathValid(String key, String path) {

        String value = (String) redisSlave2Template.opsForValue().get(key);
        if (value == null) return false;
        return value.equals(CommonsUtils.MD5(path));
    }

    @Override
    public boolean resetDatabase() throws Exception {
        //Reset redis database
        redisTemplate.opsForValue().set("21",50);

        //Truncate table & reset commodity stock
        Map<String, String> datasource = YmlUtils.getYmlByFileName("classpath:application-dev.yml", "spring", "datasource");
        Connection conn;
        if (datasource!=null){
            String url = datasource.get("spring.datasource.url");
            String username = datasource.get("spring.datasource.username");
            String password = datasource.get("spring.datasource.password");
            try {
                conn = DriverManager.getConnection(url,username,password);
            } catch (SQLException throwable) {
                throw new Exception(throwable.getMessage());
            }
        }else {
            return false;
        }
        ScriptRunner runner = new ScriptRunner(conn);
        runner.setErrorLogWriter(null);
        runner.setLogWriter(null);
        runner.runScript(Resources.getResourceAsReader("scripts/seckill.sql"));
        conn.close();
        log.info("=======Reset Database Success=======");
        return true;
    }


    private static final Integer ZERO = 0;
    /**
     * return orderId 订单0表示秒杀失败
     * @param seckillMessage
     * @param commoditySeckill
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int seckillCommodityOrder(SeckillMessage seckillMessage, CommoditySeckill commoditySeckill) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        User userInfo = new User();
        Commodity commodityDetail = new Commodity();
        try {
            //查询用户信息
            userInfo = objectMapper.convertValue(appleStoreUserClient.findUserInfoByUserId(seckillMessage.getUserId()).getData(), User.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            userInfo.setId(seckillMessage.getUserId());
            commodityDetail.setId(commoditySeckill.getCommodityId());
            CommodityOrder commodityOrder = new CommodityOrder();
            commodityOrder.fillSeckillOrder(userInfo, commoditySeckill, commodityDetail, seckillMessage.getOutTradeNo());
            commodityOrder.setState(0);
            commodityOrder.setSeckillCode(SeckillConstant.EXCEPTION);
            commodityOrder.setFailMsg("AppleStoreUserService出现异常");
            commoditySeckillMapper.seckillCommodityOrderWithFail(commodityOrder);
            //异常的seckillMessage添加到mq中
            String message = objectMapper.writeValueAsString(seckillMessage);
            rabbitTemplate.convertAndSend("exception_queue", message);
            return ZERO;
        }

        try {
            //查询商品信息
            commodityDetail = objectMapper.convertValue(appleStoreCommodityClient.findCommodityDetail(commoditySeckill.getCommodityId()).getData(), Commodity.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            commodityDetail.setId(commoditySeckill.getCommodityId());
            CommodityOrder commodityOrder = new CommodityOrder();
            commodityOrder.fillSeckillOrder(userInfo, commoditySeckill, commodityDetail, seckillMessage.getOutTradeNo());
            commodityOrder.setState(0);
            commodityOrder.setSeckillCode(SeckillConstant.EXCEPTION);
            commodityOrder.setFailMsg("AppleStoreCommodityService出现异常");
            commoditySeckillMapper.seckillCommodityOrderWithFail(commodityOrder);
            //异常的seckillMessage添加到mq中
            String message = objectMapper.writeValueAsString(seckillMessage);
            rabbitTemplate.convertAndSend("exception_queue", message);
            return ZERO;
        }


        BigDecimal orgAccount = userInfo.getAccount();
        BigDecimal finalAccount = orgAccount.subtract(commoditySeckill.getSeckillPrice());

        if (finalAccount.signum() == -1) {
            CommodityOrder commodityOrder = new CommodityOrder();
            commodityOrder.fillSeckillOrder(userInfo, commoditySeckill, commodityDetail, seckillMessage.getOutTradeNo());
            commodityOrder.setState(0);
            commodityOrder.setSeckillCode(SeckillConstant.INSUFFICIENT_BALANCE);
            commodityOrder.setFailMsg("当前用户:" + seckillMessage.getUserId() + "余额不足");
            commoditySeckillMapper.seckillCommodityOrderWithFail(commodityOrder);
            return ZERO;
        }

        JsonData resultData;
        Integer result1;
        Integer result2;

        CommodityOrder commodityOrder = new CommodityOrder();
        commodityOrder.fillSeckillOrder(userInfo, commoditySeckill, commodityDetail, seckillMessage.getOutTradeNo());
        commodityOrder.setSeckillCode(0);

        try {
            //插入Order表
            resultData = appleStoreCommodityOrderClient.commodityOrderByFeign(commodityOrder);
            result1 = resultData.getCode();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            commodityOrder.setState(0);
            commodityOrder.setSeckillCode(SeckillConstant.EXCEPTION);
            commodityOrder.setFailMsg("AppleStoreOrderService出现异常");
            commoditySeckillMapper.seckillCommodityOrderWithFail(commodityOrder);
            //异常的seckillMessage添加到mq中
            String message = objectMapper.writeValueAsString(seckillMessage);
            rabbitTemplate.convertAndSend("exception_queue", message);
            return ZERO;
        }

        //修改账户余额
        result2 = appleStoreUserClient.updateUserAccount(new UpdateAccountRequest(userInfo.getId(), finalAccount, orgAccount)).getCode();

        if (result1 + result2 == 0) {
            return (Integer) resultData.getData();
        }
        return ZERO;
    }
}
