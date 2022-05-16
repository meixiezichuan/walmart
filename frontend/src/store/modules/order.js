import storage from 'store'
import { ACCESS_TOKEN } from '@/store/mutation-types'
import { welcome } from '@/utils/util'
import {getOrdersList, modifyOrderItem, updateOrderInfo} from '@/api/order'

const order = {
  state: {
  },

  mutations: {
  },

  actions: {
    getOrdersList ({ commit }) {
      return new Promise((resolve, reject) => {
        getOrdersList().then(response => {
          const result = response.result
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    updateOrderInfo ({ commit }, OrderInfo) {
      return new Promise((resolve, reject) => {
        updateOrderInfo(OrderInfo).then(response => {
          resolve(response.data)
        }).catch(error => {
          reject(error)
        })
      })
    },

    modifyOrderItem ({ commit }, OrderInfo) {
      return new Promise((resolve, reject) => {
        modifyOrderItem(OrderInfo).then(response => {
          resolve(response.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
  }
}

export default order
