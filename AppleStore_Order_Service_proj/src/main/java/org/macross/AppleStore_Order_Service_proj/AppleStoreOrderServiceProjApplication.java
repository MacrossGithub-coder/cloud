package org.macross.AppleStore_Order_Service_proj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@EnableCircuitBreaker
@MapperScan("org.macross.AppleStore_Order_Service_proj.mapper")
@ComponentScan({"org.macross"})
public class AppleStoreOrderServiceProjApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppleStoreOrderServiceProjApplication.class, args);
	}

}
