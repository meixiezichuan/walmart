package com.walmart_6g.walmartNotificationService.controller;

import com.walmart_6g.walmartNotificationService.service.NotificationService;
import entity.Notification;
import entity.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: zhouzhong
 * @Email: 21212010059@m.fudan.edu.cn
 * @Date: 2021/11/28 19:37
 * @Description:
 */
@RestController
@RequestMapping("api/v1/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("")
    public ResponseEntity<Response<Notification>> createNotification(@RequestBody Notification notification){
        MultiValueMap<String, String> headers = new HttpHeaders();
        Response<Notification> response = notificationService.insert(notification);
        return new ResponseEntity<>(response, headers, response.getStatus());

    }


    @PutMapping("/{notification_id}")
    public ResponseEntity<Response<Notification>> updateNotification(@PathVariable String notification_id,
            HttpServletRequest httpServletRequest,@RequestBody Notification notification
                                                                         ){
        MultiValueMap<String, String> headers = new HttpHeaders();
        String userId = null;
        try{
            userId = httpServletRequest.getHeader("userId");
        }catch (Exception e){
            return new ResponseEntity(null,headers,400);
        }
        int isRead = notification.getIsRead();
        Response<Notification> response = notificationService.update(notification_id, userId, isRead);
        return new ResponseEntity<>(response, headers, response.getStatus());
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<Response<List<Notification>>> getAll(@PathVariable String user_id,
                                                               HttpServletRequest httpServletRequest){
        MultiValueMap<String, String> headers = new HttpHeaders();
        String userId = null;
        try{
            userId = httpServletRequest.getHeader("userId");
        }catch (Exception e){
            return new ResponseEntity(null,headers,400);
        }
        Response<List<Notification>> response = notificationService.list(user_id, userId);
        return new ResponseEntity<>(response, headers, response.getStatus());
    }


}