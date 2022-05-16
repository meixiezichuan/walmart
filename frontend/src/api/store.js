import request from "@/utils/request";

const api = {
  Stores: '/store'
}

export default api

export function getStores (parameter) {
  return request({
    url: api.Stores,
    method: 'get',
    params: parameter
  })
}

export function addStore (parameter) {
  return request({
    url: api.Stores,
    method: 'post',
    data: parameter
  })
}