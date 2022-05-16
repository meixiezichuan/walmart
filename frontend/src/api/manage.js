import request from '@/utils/request'

const api = {
  // user: '/user',
  role: '/role',
  permission: '/permission',
  permissionNoPager: '/permission/no-pager',
  orgTree: '/org/tree',

  userList: '/user',
  goodList: '/goods',
  modifyGoodInfo: '/goods/',
  insertGood: '/goods',
  deleteGood: '/goods/',
  GoodCategory: '/goods/category',
  modifyUserInfo: '/user',
  insertUser: '/user/register',
  deleteUser: '/user'
}

export default api

// export function getUserList (parameter) {
//   return request({
//     url: api.user,
//     method: 'get',
//     params: parameter
//   })
// }

export function getRoleList (parameter) {
  return request({
    url: api.role,
    method: 'get',
    params: parameter
  })
}

export function getUserList (parameter) {
  return request({
    url: api.userList,
    method: 'get',
    params: parameter
  })
}

export function updateUserInfo (parameter) {
  return request({
    url: api.modifyUserInfo,
    method: 'put',
    data: parameter,
  })
}

export function insertUser (parameter) {
  return request({
    url: api.insertUser,
    method: 'post',
    data: parameter,
  })
}

export function deleteUser (parameter) {
  return request({
    url: api.deleteUser,
    method: 'delete',
    data: parameter,
  })
}

export function getGoodList (parameter) {
  return request({
    url: api.goodList,
    method: 'get',
    params: parameter
  })
}

export function updateGoodInfo (parameter) {
  return request({
    url: api.modifyGoodInfo+parameter.id,
    method: 'put',
    data: parameter,
  })
}

export function insertGood (parameter) {
  return request({
    url: api.insertGood,
    method: 'post',
    data: parameter,
  })
}

export function deleteGood (parameter) {
  return request({
    url: api.deleteGood+parameter.id,
    method: 'delete',
    data: parameter,
  })
}

export function getPermissions (parameter) {
  return request({
    url: api.permissionNoPager,
    method: 'get',
    params: parameter
  })
}

export function getOrgTree (parameter) {
  return request({
    url: api.orgTree,
    method: 'get',
    params: parameter
  })
}

// id == 0 add     post
// id != 0 update  put
export function saveService (parameter) {
  return request({
    url: api.service,
    method: parameter.id === 0 ? 'post' : 'put',
    data: parameter
  })
}

export function saveSub (sub) {
  return request({
    url: '/sub',
    method: sub.id === 0 ? 'post' : 'put',
    data: sub
  })
}

export function insertGoodsCategory (parameter) {
  return request({
    url: api.GoodCategory,
    method: 'post',
    data: parameter
  })
}

export function getGoodsCategory (parameter) {
  return request({
    url: api.GoodCategory,
    method: 'get',
    params: parameter
  })
}

export function updateGoodsCategory (parameter) {
  return request({
    url: api.GoodCategory + '/' + parameter.id,
    method: 'put',
    data: parameter
  })
}

export function deleteGoodsCategory (parameter) {
  return request({
    url: api.GoodCategory + '/' + parameter.id,
    method: 'delete'
  })
}

export function addGoodsCategory (parameter) {
  return request({
    url: api.GoodCategory,
    method: 'post',
    data: parameter
  })
}