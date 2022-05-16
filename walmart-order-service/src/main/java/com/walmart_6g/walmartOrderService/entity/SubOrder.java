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

/**
 * Created on 2021/10/29  10:46 上午
 *
 * @author yang hong
 */
@Data
@TableName("sub_order")
@AllArgsConstructor
@NoArgsConstructor
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class SubOrder {
    /**
     * 订单状态：已完成
     */
    public static final int completed = 1;

    /**
     * 订单状态：未付款
     */
    public static final int unPayed = 2;

    /**
     * 订单状态：未发货
     */
    public static final int unDelivered = 3;

    /**
     * 订单状态：未收货
     */
    public static final int unReceived = 4;

    /**
     * 订单状态：已取消
     */
    public static final int cancelled = 5;

    @IsKey
    @TableId(type = IdType.UUID)
    @Column(length = 36)
    private String id;
    @Column(name="order_id", length = 36)
    private String orderId;
    @Column(name="store_id", length = 36)
    private String storeId;
    @TableField("status")
    private Integer status;
    @TableField("total_price")
    private Float totalPrice;
    @Column(name="address_id", length = 36)
    private String addressId;
    @TableField("deprecated")
    private Boolean deprecated;
    @Column(name="seller_id", length = 36)
    private String sellerId;
    @Column(name="logistics_company_id", length = 36)
    private String logisticsCompanyId;
}
