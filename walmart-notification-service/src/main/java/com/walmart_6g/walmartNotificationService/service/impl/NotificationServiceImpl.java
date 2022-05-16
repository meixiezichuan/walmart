package com.walmart_6g.walmartNotificationService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart_6g.walmartNotificationService.feignInterface.IUserService;
import com.walmart_6g.walmartNotificationService.mapper.NotificationMapper;
import com.walmart_6g.walmartNotificationService.service.NotificationService;
import entity.Notification;
import entity.Response;
import entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author: zhouzhong
 * @Email: 21212010059@m.fudan.edu.cn
 * @Date: 2021/11/28 19:31
 * @Description:
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private IUserService iUserService;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public Response<Notification> insert(Notification notification) {
        LOGGER.info("[{}]开始运行","insert");
        ResponseEntity<Response> response= iUserService.getAdmin();
        Response response_ = response.getBody();

        if(response_.getStatus()!=200){
            return new Response<>(406, "不存在管理员用户", null);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.convertValue(response_.getData(), User.class);
        if(!notification.getSender().equals(user.getId())){
            return new Response<>(406, "只能由管理员发送信息", null);
        }
        if(notification.getReceiver().equals("")){
            return new Response<>(406, "消息接收方不能为空", null);
        }
        if(notification.getCreateTime()==null) {
            return new Response<>(406, "时间不能为空", null);
        }
        if(notification.getContent().equals("")){
            return new Response<>(406, "具体消息内容不能为空", null);
        }
        String uuid = UUID.randomUUID().toString();
        notification.setIsRead(0);
        notification.setId(uuid);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStringParse = sdf.format(Calendar.getInstance().getTime());
        notification.setCreateTime(dateStringParse);
        int status = notificationMapper.insert(notification);
        if (status==1){
            return new Response<>(200, "新增成功", notification);
        }
        return new Response<>(406, "新增消息错误", null);
    }

    @Override
    public Response<Notification> update(String notificationID, String userID, int isRead) {
        LOGGER.info("[{}]开始运行","update");
        if (notificationID.equals("")){
            return new Response<>(406, "notificationID不能为空", null);
        }
        Notification notification = notificationMapper.selectById(notificationID);
        if(notification==null){
            return new Response<>(406, "notificationID错误", null);
        }
        if (!notification.getReceiver().equals(userID)) {
            return new Response<>(406, "userID 和 receiverID 不一样", null);
        }
        if (isRead !=1){
            return new Response<>(406, "isRead错误", null);
        }
        if (notification.getIsRead()!=0){
            return new Response<>(406, "消息已经被读取", null);
        }
        notification.setIsRead(isRead);
        int status = notificationMapper.updateById(notification);
        if (status==1){
            Notification notificationdata = notificationMapper.selectById(notificationID);
            return new Response<>(200,"更新成功",notificationdata);
        }else{
            return new Response<>(406,"更新notification失败",null);
        }

    }

    @Override
    public Response<List<Notification>> list(String user_id, String userID) {
        LOGGER.info("[{}]开始运行","list");
        if(user_id.equals("")){
            return new Response<>(406, "userID不能为空", null);
        }
        if(!user_id.equals(userID)){
            return new Response<>(406, "不能查看非自己的消息", null);
        }
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver",user_id);
        queryWrapper.orderByDesc("create_time");
        List<Notification>  notificationList = notificationMapper.selectList(queryWrapper);
        return new Response<>(200, "查询成功", notificationList);
    }

}