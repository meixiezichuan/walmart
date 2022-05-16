package com.walmart_6g.walmartOrderService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsKey;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableCharset;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableEngine;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;
import lombok.Data;

@Data
@TableName("address")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class Address {
    @IsKey
    @TableId(type = IdType.UUID)
    @Column(length = 36)
    private String id;
    @Column(name="user_id", length = 36)
    private String userId;
    @TableField("name")
    private String name;
    @TableField("phone_num")
    private String phoneNum;
    @TableField("consign_info")
    private String consignInfo;
    @TableField("deprecated")
    private Boolean deprecated;
}
