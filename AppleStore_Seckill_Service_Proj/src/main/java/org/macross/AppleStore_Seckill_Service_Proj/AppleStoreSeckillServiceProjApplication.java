package org.macross.AppleStore_Seckill_Service_Proj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@EnableCircuitBreaker
@MapperScan("org.macross.AppleStore_Seckill_Service_Proj.mapper")
@ComponentScan({"org.macross"})
public class AppleStoreSeckillServiceProjApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppleStoreSeckillServiceProjApplication.class, args);
	}

}
