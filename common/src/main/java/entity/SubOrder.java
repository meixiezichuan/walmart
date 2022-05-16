package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 2021/10/29  10:46 上午
 *
 * @author yang hong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    private String id;
    private String orderId;
    private String storeId;
    private Integer status;
    private Float totalPrice;
    private String addressId;
    private Boolean deprecated;
    private String sellerId;
    private String logisticsCompanyId;
}
