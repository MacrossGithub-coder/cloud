package org.macross.AppleStore_Seckill_Service_Proj;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.jupiter.api.Test;
import org.macross.AppleStore_Seckill_Service_Proj.utils.YmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

@SpringBootTest
class AppleStoreSeckillServiceProjApplicationTests {

    @Autowired
    @Qualifier("redisTemplateMasterSeckill")
    private RedisTemplate<String, Object> redisTemplate;

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

}
