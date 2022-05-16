import storage from 'store'
import { login, getInfo, logout, register } from '@/api/auth'
import { ACCESS_TOKEN } from '@/store/mutation-types'
import { welcome } from '@/utils/util'
import { updateUserInfo, insertUser, deleteUser } from '@/api/manage'
import {constantRouterMap} from "@/config/router.config";

const user = {
  state: {
    token: '',
    name: '',
    username: '',//用户名
    welcome: '',
    avatar: '',
    roleId: '',//role id
    roles: [],
    info: {},
    routers: constantRouterMap,
    addRouters: []
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_NAME: (state, { name, welcome }) => {
      state.name = name
      state.welcome = welcome
    },
    SET_USERNAME: (state, username) => {
      state.username = username
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
    },
    SET_ROLEID: (state, roleId) => {
      state.roleId = roleId
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles
    },
    SET_INFO: (state, info) => {
      state.info = info
    },
    SET_ROUTERS: (state, routers) => {
      state.addRouters = routers
      state.routers = constantRouterMap.concat(routers)
    }
  },

  actions: {
    // 登录
    Login ({ commit }, userInfo) {
      return new Promise((resolve, reject) => {
        login(userInfo).then(response => {

          const token = response.headers.token
          storage.set(ACCESS_TOKEN, token, 2 * 24 * 60 * 60 * 1000)
          const data = response.data

          storage.set('user', data.data)
          resolve(data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    GetInfo ({ commit }) {
      return new Promise((resolve, reject) => {
        getInfo().then(response => {
          const result = response.result

          if (result.roleId) {
            commit('SET_ROLEID', result.roleId)
            commit('SET_INFO', result)
          } else {
            reject(new Error('getInfo: roles must be a non-null array !'))
          }

          commit('SET_NAME', { name: result.name, welcome: welcome() })
          commit('SET_AVATAR', result.avatar)

          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 登出
    Logout ({ commit, state }) {
      return new Promise((resolve) => {
        storage.remove(ACCESS_TOKEN)
        storage.remove('user')
        commit('SET_ROUTERS', [])
        resolve()
      })
    },

    //注册
    Register ({ commit }, userInfo) {
      return new Promise((resolve, reject) => {
        register(userInfo).then(response => {
          resolve(response.data)
        }).catch(error => {
          reject(error)
        })
      })
    },

    UpdateUserInfo ({ commit }, userInfo) {
      return new Promise((resolve, reject) => {
        updateUserInfo(userInfo).then(response => {
          resolve(response.data)
        }).catch(error => {
          reject(error)
        })
      })
    },

    insertUser ({ commit }, userInfo) {
      return new Promise((resolve, reject) => {
        insertUser(userInfo).then(response => {
          resolve(response.data)
        }).catch(error => {
          reject(error)
        })
      })
    },

    deleteUser ({ commit }, userInfo) {
      return new Promise((resolve, reject) => {
        deleteUser(userInfo).then(response => {
          resolve(response.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
  }
}

export default user
