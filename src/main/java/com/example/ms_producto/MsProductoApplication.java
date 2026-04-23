package com.example.ms_producto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsProductoApplication {
	public static void main(String[] args) {
		SpringApplication.run(MsProductoApplication.class, args);
	}
}