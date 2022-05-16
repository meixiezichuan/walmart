package entity;

import lombok.Data;

import java.sql.Timestamp;

/**
 * Created on 2021/10/27  10:32 上午
 *
 * @author yang hong
 */
@Data
public class Order {
    private String id;
    private String userId;
    private Timestamp createTime;
    private Float totalPrice;
    private String addressId;
}
