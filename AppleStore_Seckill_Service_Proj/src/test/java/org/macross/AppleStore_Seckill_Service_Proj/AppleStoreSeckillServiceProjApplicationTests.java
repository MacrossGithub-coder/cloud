package org.macross.AppleStore_Seckill_Service_Proj;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.jupiter.api.Test;
import org.macross.AppleStore_Common_Config.model.entity.CommodityOrder;
import org.macross.AppleStore_Seckill_Service_Proj.mapper.CommoditySeckillMapper;
import org.macross.AppleStore_Seckill_Service_Proj.utils.FreemarkerUtil;
import org.macross.AppleStore_Seckill_Service_Proj.utils.YmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

@SpringBootTest
class AppleStoreSeckillServiceProjApplicationTests {


    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    @Qualifier("redisTemplateMasterSeckill")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CommoditySeckillMapper commoditySeckillMapper;

    @Test
    void runSqlScript() throws Exception {
        //Reset redis database
        redisTemplate.opsForValue().set("21",50);

        //Truncate table & reset commodity stock
        Map<String, String> datasource = YmlUtils.getYmlByFileName("D:\\Users\\i_zhangjiancheng\\IdeaProjects\\cloud\\AppleStore_Seckill_Service_Proj\\src\\main\\resources\\application-dev.yml", "spring", "datasource");
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
            return;
        }
        ScriptRunner runner = new ScriptRunner(conn);
        runner.setErrorLogWriter(null);
        runner.setLogWriter(null);
        runner.runScript(Resources.getResourceAsReader("scripts/seckill.sql"));
        conn.close();
        System.out.println("=======Success=======");
    }

    @Test
    public void testSendWithTemplate() {

//        String templateContent = FreemarkerUtil.getTemplateContent("/ftl/job-failure.html");
//        Map<String, Object> root = new HashMap<>();
//        root.put("operator", "System");
//        root.put("subject", "标题");
//        String outputContent = FreemarkerUtil.parse(templateContent, root);

        CommodityOrder seckillResult = commoditySeckillMapper.findSeckillResult("6fe46247-a6a3-452d-9c60-a20789971a67");
        String templateContent = FreemarkerUtil.getTemplateContent("/ftl/job-failure.html");
        Map map = JSON.parseObject(JSON.toJSONString(seckillResult), Map.class);
        String outputContent = FreemarkerUtil.parse(templateContent, map);
        MimeMessage mimeMailMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage);
        try {
            mimeMessageHelper.setFrom("macross_af@163.com");
            mimeMessageHelper.setTo("macross_af@163.com");
            mimeMessageHelper.setSubject("AppleStore微服务系统异常邮件");
            mimeMessageHelper.setText(outputContent, true);
            mailSender.send(mimeMailMessage);
            System.out.println("邮件发送成功");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

}
