package com.walmart_6g.walmartCartService.entity;

import entity.GoodsDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class CartItemDetail extends GoodsDetail  {
    private Integer num;
    private String updateTime;

    public CartItemDetail(GoodsDetail gd, Integer n, String update_time) {
        super(gd.getCategoryId(), gd.getCategoryName(), gd.getStockNum(), gd.getPrice(),
                gd.getGoodsId(), gd.getGoodsName(), gd.getGoodsImage(), gd.getStoreId(), gd.getStoreName(), gd.getSellerId(), gd.getSellerName());
        num = n;
        updateTime = update_time;
    }
}
