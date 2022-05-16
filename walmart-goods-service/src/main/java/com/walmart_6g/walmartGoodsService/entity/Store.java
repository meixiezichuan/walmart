package com.walmart_6g.walmartGoodsService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity(name = "store")
@Table(name = "store")
@NoArgsConstructor
@AllArgsConstructor
@GenericGenerator(name = "jpa-uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class Store {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(name = "id",length = 36)
    private String id;

    @NotNull
    @Column(name = "name", length = 256, nullable = false)
    private String name;

    @Column(name = "description", length = 2048)
    private String description;

    @Column(name = "user_id",length = 36)
    private String userId;

    @Column(name = "create_time",length = 36)
    private String createTime;

    @Column(name = "grade")
    private int grade;

    @Column(name = "status",length = 36)
    private String status;
}
