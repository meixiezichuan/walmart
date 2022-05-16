package com.walmart_6g.walmartNotificationService;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: zhouzhong
 * @Email: 21212010059@m.fudan.edu.cn
 * @Date: 2021/11/28 20:51
 * @Description:
 */
@EnableFeignClients
@SpringBootApplication
@MapperScan(basePackages = {"com.walmart_6g.walmartNotificationService.mapper", "com.gitee.sunchenbin.mybatis.actable.dao.*"})
@ComponentScan(basePackages = {"com.walmart_6g.walmartNotificationService", "com.gitee.sunchenbin.mybatis.actable.manager.*"})

public class WalmartNotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WalmartNotificationServiceApplication.class, args);
    }
}