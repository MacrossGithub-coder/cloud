package org.macross.AppleStore_User_Service_Proj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("org.macross.AppleStore_User_Service_Proj.mapper")
@ComponentScan({"org.macross"})
public class AppleStoreUserServiceProjApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppleStoreUserServiceProjApplication.class, args);
	}

}
