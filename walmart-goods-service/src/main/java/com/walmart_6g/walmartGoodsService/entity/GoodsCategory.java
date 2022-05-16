package com.walmart_6g.walmartGoodsService.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Data
@Entity(name = "goods_category")
@Table(name = "goods_category")
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert(value = true)
@DynamicUpdate(value = true)
@GenericGenerator(name = "jpa-uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class GoodsCategory {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(name = "id",length = 36)
    private String id;

    @NotNull
    @Column(name = "goods_id", length = 36, nullable = false)
    private String goodsId;

    @NotNull
    @Column(name= "name", nullable = false)
    private String name;

    @Min(value = 0)
    @Column(name = "num", nullable = false, columnDefinition = "int default 0")
    private Integer num;

    @Min(value = 0)
    @Column(name = "price", nullable = false, columnDefinition = "float(10,2) default 0")
    private Float price;

    public boolean CheckValid() {
        if(name == null || goodsId == null || price == null || price < 0 ){
            return false;
        }
        return true;
    }

}
