import request  from '@/utils/request'
import storage from "store";

const api = {
  notificationList: '/notification',
  modifyNotification: '/notification'
}

export default api

export function getNotificationList () {
  return request({
    url: api.notificationList + '/' + storage.get('user').id,
    method: 'get'
  })
}

export function modifyNotification (notification_id, parameter) {
  return request({
    url: api.modifyNotification + '/' + notification_id,
    method: 'put',
    data: parameter
  })
}