package com.walmart_6g.walmartNotificationService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.*;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;
import lombok.Data;

import java.util.Date;

/**
 * @Author: zhouzhong
 * @Email: 21212010059@m.fudan.edu.cn
 * @Date: 2021/11/28 19:32
 * @Description:
 */
@Data
@TableName("notification")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class Notification {
    @Column
    @IsKey
    @TableId(type = IdType.UUID)
    private String id;

    @Column
    @IsNotNull
    private String sender;

    @Column
    @IsNotNull
    private String receiver;

    @Column
    @IsNotNull
    private String content;

    @Column
    @IsNotNull
    private String createTime;

    @Column
    @IsNotNull
    private int isRead;
}