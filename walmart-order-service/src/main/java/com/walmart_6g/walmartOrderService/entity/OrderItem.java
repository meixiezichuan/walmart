package com.walmart_6g.walmartOrderService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Index;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableCharset;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableEngine;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 2021/10/27  2:27 下午
 *
 * @author yang hong
 */
@Data
@TableName("order_item")
@AllArgsConstructor
@NoArgsConstructor
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class OrderItem {
    @TableId(type = IdType.UUID)
    @Column(length = 36)
    private String id;

    @Index
    @Column(name="sub_order_id", length = 36)
    private String subOrderId;
    @Column(name="goods_id", length = 36)
    private String goodsId;
    @Index
    @Column(name="goods_category_id", length = 36)
    private String goodsCategoryId;
    @TableField("goods_name")
    private String goodsName;
    @TableField("goods_description")
    private String goodsDescription;
    @TableField("goods_image")
    private String goodsImage;
    @TableField("goods_category_name")
    private String goodsCategoryName;
    @TableField("status")
    private int status;
    @TableField("num")
    private Integer num;
    @TableField("price")
    private Float price;
    @TableField("deprecated")
    private Boolean deprecated;
}
