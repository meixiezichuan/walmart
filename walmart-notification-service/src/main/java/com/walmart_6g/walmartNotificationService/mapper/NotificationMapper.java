package com.walmart_6g.walmartNotificationService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author: zhouzhong
 * @Email: 21212010059@m.fudan.edu.cn
 * @Date: 2021/11/28 19:35
 * @Description:
 */
@Mapper
@Repository
public interface NotificationMapper extends BaseMapper<Notification> {
}