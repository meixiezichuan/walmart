package com.walmart_6g.walmartOrderService;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author yanghong
 */
@SpringBootApplication
@EnableSwagger2
@EnableFeignClients
@MapperScan(basePackages = {"com.walmart_6g.walmartOrderService.mapper","com.gitee.sunchenbin.mybatis.actable.dao.*"})
@ComponentScan(basePackages = {"com.walmart_6g.walmartOrderService","com.gitee.sunchenbin.mybatis.actable.manager.*"})
public class WalmartOrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WalmartOrderServiceApplication.class, args);
    }
}
