package com.walmart_6g.walmartLogisticsService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.*;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;
import lombok.Data;

/**
 * @Author: zhouzhong
 * @Email: 21212010059@m.fudan.edu.cn
 * @Date: 2021/11/29 12:38
 * @Description:
 */
@Data
@TableName("logistics_company")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class LogisticsCompany {
    @Column
    @IsKey
    @TableId(type = IdType.UUID)
    private String id;

    @Column
    @IsNotNull
    private String name;

    @Column
    @IsNotNull
    private int deprecated;
}