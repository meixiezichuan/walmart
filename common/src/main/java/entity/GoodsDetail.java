package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SuperBuilder(toBuilder = true)

public class GoodsDetail {

    private String categoryId;
    private String categoryName;
    private Integer stockNum;
    private Float price;
    private String goodsId;
    private String goodsName;
    private String goodsImage;
    private String storeId;
    private String storeName;
    private String sellerId;
    private String sellerName;
}