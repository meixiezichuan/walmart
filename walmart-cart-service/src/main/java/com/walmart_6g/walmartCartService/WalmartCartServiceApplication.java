package com.walmart_6g.walmartCartService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class WalmartCartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalmartCartServiceApplication.class, args);
    }

}
