package com.walmart_6g.walmartUserService;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EntityScan("com.walmart_6g.walmartUserService.entity")
@MapperScan(basePackages = {"com.walmart_6g.walmartUserService.mapper", "com.gitee.sunchenbin.mybatis.actable.dao.*"})
@ComponentScan(basePackages = {"com.walmart_6g.walmartUserService.config","com.walmart_6g.walmartUserService.controller", "com.walmart_6g.walmartUserService.service","com.gitee.sunchenbin.mybatis.actable.manager.*"})
public class WalmartUserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WalmartUserServiceApplication.class, args);
    }
}
