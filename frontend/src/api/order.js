import request  from '@/utils/request'

const api = {
  orderList: '/order/subOrder/list',
  updateOrderInfo: '/order/subOrder',
  createOrder: '/order',
  modifyOrderItem: '/order/orderItem',
  payOrder: '/order/payment',
  paySubOrder: '/order/subpayment',

  changeOrderOfBuyer: '/order/subOrder/buyer',
  changeOrderOfSeller: '/order/subOrder/seller'
}

export default api

export function getOrdersList (parameter) {
  return request({
    url: api.orderList,
    method: 'get',
    params: parameter
  })
}

export function updateOrderInfo (parameter) {
  return request({
    url: api.updateOrderInfo,
    method: 'put',
    data: parameter,
  })
}

export function createOrder (parameter) {
  return request({
    url: api.createOrder,
    method: 'post',
    data: parameter,
  })
}

export function modifyOrderItem (parameter) {
  return request({
    url: api.modifyOrderItem,
    method: 'put',
    data: parameter,
    headers:{"userId": storage.get("user").id, "role": storage.get("user").role}
  })
}

export function payOrder (parameter) {
  return request({
    url: api.payOrder + '/' + parameter,
    method: 'put',
  })
}

export function paySubOrder (parameter) {
  return request({
    url: api.paySubOrder + '/' + parameter,
    method: 'put',
  })
}

export function changeOrderOfBuyer (sub_order_id, parameter) {
  return request({
    url: api.changeOrderOfBuyer + '/' + sub_order_id,
    method: 'put',
    data: parameter
  })
}

export function changeOrderOfSeller (sub_order_id, parameter) {
  return request({
    url: api.changeOrderOfSeller + '/' + sub_order_id,
    method: 'put',
    data: parameter
  })
}