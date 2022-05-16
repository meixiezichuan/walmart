package com.walmart_6g.walmartWalletService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class WalmartWalletServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalmartWalletServiceApplication.class, args);
    }

}
