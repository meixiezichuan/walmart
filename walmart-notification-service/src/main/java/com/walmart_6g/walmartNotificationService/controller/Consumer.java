package com.walmart_6g.walmartNotificationService.controller;

import com.walmart_6g.walmartNotificationService.service.NotificationService;
import entity.Notification;
import entity.Response;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @Author: zhouzhong
 * @Email: 21212010059@m.fudan.edu.cn
 * @Date: 2021/11/28 20:34
 * @Description:
 */
@Component
@RabbitListener(queues = "DirectQueue")
public class Consumer {

    @Autowired
    private NotificationService notificationService;

    @RabbitHandler
    public void process(Map messageMap){
        String receiver = messageMap.get("receiver").toString();
        String content = messageMap.get("content").toString();
        String sender = messageMap.get("sender").toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStringParse = sdf.format(Calendar.getInstance().getTime());
        Notification notification = new Notification();
        notification.setReceiver(receiver);
        notification.setContent(content);
        notification.setSender(sender);
        notification.setCreateTime(dateStringParse);
        Response<Notification> response = notificationService.insert(notification);
        System.out.println(response.getData());
        System.out.println(response.getDetail());
        System.out.println("处理了一次");
    }
}