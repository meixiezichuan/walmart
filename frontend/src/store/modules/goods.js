import {getGoodsList} from '@/api/buyer'

const goods = {
  state: {},
  actions: {
    getGoods() {
      return new Promise((resolve, reject) => {
        getGoodsList().then(response => {
          console.log(response)
        }).catch(error => {
          reject(error)
        })
      })
    }
  }
}

export default goods