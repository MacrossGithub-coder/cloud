package org.macross.AppleStore_Commodity_Service_Proj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("org.macross.AppleStore_Commodity_Service_Proj.mapper")
@ComponentScan({"org.macross"})
public class AppleStoreCommodityServiceProjApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppleStoreCommodityServiceProjApplication.class, args);
	}

}
