package org.macross.AppleStore_Seckill_Service_Proj.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.macross.AppleStore_Common_Config.model.entity.CommodityOrder;
import org.macross.AppleStore_Seckill_Service_Proj.mapper.CommoditySeckillMapper;
import org.macross.AppleStore_Seckill_Service_Proj.utils.FreemarkerUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Map;

/**
 * @Author: jiancheng.zhang
 * @Date: 2021/4/16 14:54
 */
@Component
@Slf4j
public class ExceptionConsumer {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CommoditySeckillMapper commoditySeckillMapper;

    @RabbitListener(queuesToDeclare = @Queue("exception_queue"))
    public void exceptionConsumer(String data, Channel channel, Message message) throws IOException, MessagingException {
        log.info("============= Exception Consumer 开始处理异常消息==================");
        ObjectMapper objectMapper = new ObjectMapper();
        SeckillMessage seckillMessage = objectMapper.readValue(data.getBytes(), SeckillMessage.class);
        CommodityOrder seckillResult = commoditySeckillMapper.findSeckillResult(seckillMessage.getOutTradeNo());
        String templateContent = FreemarkerUtil.getTemplateContent("/ftl/job-failure.html");

        Map map = JSON.parseObject(JSON.toJSONString(seckillResult), Map.class);
        map.put("createTime",seckillResult.getCreateTime());
        String outputContent = FreemarkerUtil.parse(templateContent, map);
        MimeMessage mimeMailMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage);
        try {
            mimeMessageHelper.setFrom("macross_af@163.com");
            mimeMessageHelper.setTo("macross_af@163.com");
            mimeMessageHelper.setSubject("AppleStore微服务系统异常邮件");
            mimeMessageHelper.setText(outputContent, true);
            mailSender.send(mimeMailMessage);
            log.info("邮件发送成功");
        } catch (MessagingException e) {
            log.error("Consumer1处理消息异常，消息重新回到队列");
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            throw e;
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

}
