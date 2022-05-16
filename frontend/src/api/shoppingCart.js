import request  from '@/utils/request'
import storage from "store";

const api = {
  cart: '/cart/' + storage.get('user').id,
  deleteGoods: '/cart/delete/'  + storage.get('user').id
}

export default api

export function getCartGoodsList (parameter) {
  return request({
    url: api.cart,
    method: 'get',
    params: parameter
  })
}

export function addGoodsToCart (parameter) {
  return request({
    url: api.cart,
    method: 'post',
    data: parameter
  })
}

export function changeGoodsNumInCart (parameter) {
  return request({
    url: api.cart,
    method: 'put',
    data: parameter
  })
}

export function removeGoodsFromCart (parameter) {
  return request({
    url: api.deleteGoods,
    method: 'post',
    data: parameter
  })
}