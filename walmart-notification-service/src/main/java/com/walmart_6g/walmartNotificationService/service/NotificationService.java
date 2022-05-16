package com.walmart_6g.walmartNotificationService.service;

import entity.Notification;
import entity.Response;

import java.util.List;

public interface NotificationService {
    public Response<Notification> insert(Notification notification);

    public Response<Notification> update(String notificationID, String userID, int isRead);

    public Response<List<Notification>> list(String user_id, String userId);
}
