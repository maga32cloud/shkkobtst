package com.cafe.mobile.shcafe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShcafeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShcafeApplication.class, args);
	}

}
