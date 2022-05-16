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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created on 2021/10/27  10:32 上午
 *
 * @author yang hong
 */
@Data
@TableName("orders")
@AllArgsConstructor
@NoArgsConstructor
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class Order {
    @IsKey
    @TableId(type = IdType.UUID)
    @Column(length = 36)
    private String id;
    @Column(name="user_id", length = 36)
    private String userId;
    @TableField("create_time")
    private Timestamp createTime;
    @TableField("total_price")
    private Float totalPrice;
    @Column(name="address_id", length = 36)
    private String addressId;
}
