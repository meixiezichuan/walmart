import request  from '@/utils/request'
import storage from "store";

const api = {
  goodsList: '/goods',
  updateUser: '/user',
  userInfo: '/user/id',
  wallet: '/wallet'
}

export default api

export function getGoodsList(parameter) {
  return request({
    url: api.goodsList,
    method: 'get',
    params: parameter
  })
}

export function getUserByToken(parameter) {
  return request({
    url: api.userInfo,
    method: 'get',
    params: parameter
  })
}

export function updateUserInfo(parameter){
  return request({
    url: api.updateUser,
    method: 'put',
    data: parameter
  })
}

export function getWallet(){
  return request({
    url: api.wallet + '/' + storage.get('user').id,
    method: 'get'
  })
}

export function updateWallet(parameter){
  return request({
    url: api.wallet + '/' + parameter.userId,
    method: 'put',
    data: parameter
  })
}
