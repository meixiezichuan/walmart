package com.walmart_6g.walmartCartService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Entity(name = "cartItem")
@Table(name = "cart_item", indexes = {@Index(columnList = "user_id")})
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    @NotNull
    @Column(name = "user_id", length = 36, nullable = false)
    public String userId;

    @NotNull
    @Column(name = "category_id", length = 36, nullable = false)
    public String categoryId;

    @Min(value = 0)
    @Column(name = "num", columnDefinition = "int default 0")
    private Integer num;

    @Column(name = "update_time", length = 36, nullable = false)
    private String updateTime;

    public CartItem(String user_id, String category_id, Integer n) {
        userId = user_id;
        categoryId = category_id;
        num = n;

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        updateTime = formatter.format(date);
    }

    public void UpdateTime() {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        updateTime = formatter.format(date);
    }

    public boolean CheckValid() {
        if(categoryId== null || num < 1) {
            return false;
        }
        return true;
    }
}

