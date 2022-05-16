package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 2021/10/27  2:27 下午
 *
 * @author yang hong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private String id;
    private String subOrderId;
    private String goodsId;
    private String goodsCategoryId;
    private String goodsName;
    private String goodsDescription;
    private String goodsImage;
    private String goodsCategoryName;
    private int status;
    private Integer num;
    private Float price;
    private Boolean deprecated;
}
