package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsCategory {

    private String id;

    private String goodsId;

    private String name;

    private Integer num;

    private Float price;



}
