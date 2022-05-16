package com.walmart_6g.walmartLogisticsService;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: zhouzhong
 * @Email: 21212010059@m.fudan.edu.cn
 * @Date: 2021/11/29 12:45
 * @Description:
 */

@SpringBootApplication
@MapperScan(basePackages = {"com.walmart_6g.walmartLogisticsService.mapper", "com.gitee.sunchenbin.mybatis.actable.dao.*"})
@ComponentScan(basePackages = {"com.walmart_6g.walmartLogisticsService","com.gitee.sunchenbin.mybatis.actable.manager.*"})
public class WalmartLogisticsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WalmartLogisticsServiceApplication.class, args);
    }

}