import storage from 'store'
import { ACCESS_TOKEN } from '@/store/mutation-types'
import { welcome } from '@/utils/util'
import { updateGoodInfo, insertGood, deleteGood } from '@/api/manage'

const good = {
  state: {
    id: '',
    name: '',
    description: '',
    image: '',
    num: '',
    userId: '',
    deprecated: [],
  },

  mutations: {
  },

  actions: {
    GetInfo ({ commit }) {
      return new Promise((resolve, reject) => {
        getInfo().then(response => {
          const result = response.result
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    UpdateGoodInfo ({ commit }, GoodInfo) {
      return new Promise((resolve, reject) => {
        updateGoodInfo(GoodInfo).then(response => {
          resolve(response.data)
        }).catch(error => {
          reject(error)
        })
      })
    },

    insertGood ({ commit }, GoodInfo) {
      return new Promise((resolve, reject) => {
        insertGood(GoodInfo).then(response => {
          resolve(response.data)
        }).catch(error => {
          reject(error)
        })
      })
    },

    deleteGood ({ commit }, GoodInfo) {
      return new Promise((resolve, reject) => {
        deleteGood(GoodInfo).then(response => {
          resolve(response.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
  }
}

export default good
