package com.walmart_6g.walmartUserService.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.gitee.sunchenbin.mybatis.actable.annotation.*;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Table(value = "user", comment = "user table")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
@Entity(name = "user")
public class User {
    public static int admin_role = 1;
    public static int seller_role = 2;

    @TableId(type = IdType.UUID)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",length = 36)
    private String id;

    @Index
    @IsNotNull
    @Column(name = "name",length = 256)
    private String name;

    @IsNotNull
    @Column(name = "pwd",length = 256)
    private String pwd;

    @Column(name = "role")
    private int role;

    @Column(name = "email",length = 1024)
    private String email;

    @Column(name = "phone_num",length = 1024)
    private String phone_num;

    @Column(name = "address",length = 1024)
    private String address;
}
