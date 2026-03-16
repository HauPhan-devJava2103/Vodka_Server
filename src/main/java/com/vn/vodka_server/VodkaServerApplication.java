package com.vn.vodka_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VodkaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VodkaServerApplication.class, args);
	}

}
