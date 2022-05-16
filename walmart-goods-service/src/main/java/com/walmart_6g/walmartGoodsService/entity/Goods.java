package com.walmart_6g.walmartGoodsService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity(name = "goods")
@Table(name = "goods", indexes = {@Index(columnList = "name")})
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@GenericGenerator(name = "jpa-uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class Goods {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(name = "id",length = 36)
    private String id;

    @NotNull
    @Column(name = "name", length = 256, nullable = false)
    private String name;

    @Column(name = "description", length = 2048)
    private String description;

    @Column(name = "image", length = 1024)
    private String image;

//    @Min(value = 0)
//    @Column(name = "num", nullable = false, columnDefinition = "int default 0")
//    private Integer num;

    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @Column(name = "store_id", length = 36, nullable = false)
    private String storeId;

    @Column(name = "deprecated", columnDefinition = "tinyint(1) default 0")
    private Boolean deprecated;

    @Transient
    private String userName;
    @Transient
    private String storeName;

    public boolean CheckValid() {
        if(name == null || storeId == null ){
            return false;
        }
        return true;
    }
}
