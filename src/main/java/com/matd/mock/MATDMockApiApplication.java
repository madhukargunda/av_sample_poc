package com.matd.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MATDMockApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MATDMockApiApplication.class, args);
	}
}
