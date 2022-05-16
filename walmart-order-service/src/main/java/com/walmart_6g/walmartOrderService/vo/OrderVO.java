package com.walmart_6g.walmartOrderService.vo;

import com.walmart_6g.walmartOrderService.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created on 2021/10/29  9:49 上午
 *
 * @author yang hong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVO {
    private String id;
    private Integer status;
    private Float totalPrice;
    private String consignInfo;
    private List<OrderItem> orderItems;
    private String createTime;
    private String addressId;
    private String storeId;
    private String storeName;
    private String logisticsCompanyId;
    private String logisticsCompanyName;
    private Boolean deprecated;
}
