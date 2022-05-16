package com.walmart_6g.walmartUserService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.*;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@TableName("role_access")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
@Entity(name = "role_access")
public class RoleAccess {

    @TableId(type = IdType.AUTO)
    @Id
    @IsAutoIncrement
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",length = 36)
    private long id;

    @IsNotNull
    @Column(name = "role_id",length = 36)
    private int role_id;

    @Index
    @IsNotNull
    @Column(name = "url",length = 1024)
    private String url;
}
