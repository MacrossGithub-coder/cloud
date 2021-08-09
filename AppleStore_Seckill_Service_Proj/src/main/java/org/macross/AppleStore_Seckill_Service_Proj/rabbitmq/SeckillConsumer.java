package org.macross.AppleStore_Seckill_Service_Proj.rabbitmq;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.macross.AppleStore_Common_Config.model.entity.CommoditySeckill;
import org.macross.AppleStore_Common_Config.model.entity.SeckillOrder;
import org.macross.AppleStore_Seckill_Service_Proj.mapper.CommoditySeckillMapper;
import org.macross.AppleStore_Seckill_Service_Proj.service.CommoditySeckillService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

//@RabbitListener(bindings = @QueueBinding(value = @Queue,
//        key = {"seckill"},
//        exchange = @Exchange(name = "seckill_queue")))

@Component
@Slf4j
public class SeckillConsumer {

    @Autowired
    CommoditySeckillMapper commoditySeckillMapper;

    @Autowired
    CommoditySeckillService commoditySeckillService;


    @RabbitListener(queuesToDeclare = @Queue("seckill_queue"))
    public void Consumer(String data, Channel channel,Message message) throws IOException {

        System.out.println("=============Consumer 1==================");
        ObjectMapper objectMapper = new ObjectMapper();
        SeckillMessage seckillMessage = objectMapper.readValue(data.getBytes(), SeckillMessage.class);
        CommoditySeckill commoditySeckill = commoditySeckillMapper.findStockByCommodityId(seckillMessage.getCommodityId());
        if (commoditySeckill.getStock() <= 0) {
            log.info("商品库存不足。");
            return;
        }

        SeckillOrder seckillOrder = commoditySeckillMapper.findSeckillOrder(seckillMessage.getUserId(), seckillMessage.getCommodityId());
        //双重检测
        if (seckillOrder != null) {
            log.info("用户存在重复下单行为。");
            return;
        }
        try {
            commoditySeckillService.doSeckillService(seckillMessage,commoditySeckill);
        }catch (Exception e){
            log.error("Consumer1处理消息异常，消息重新回到队列");
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            throw e;
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

//    @RabbitListener(queuesToDeclare = @Queue("seckill_queue"))
//    public void Consumer2(String data, Channel channel,Message message) throws IOException {
//
//        System.out.println("=============Consumer 2==================");
//        System.out.println(data);
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
//    }
}
