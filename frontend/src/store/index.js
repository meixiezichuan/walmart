import Vue from 'vue'
import Vuex from 'vuex'

import app from './modules/app'
import user from './modules/user'
import good from './modules/good'
import order from './modules/order'

// default router permission control
// import permission from './modules/permission'

// dynamic router permission control (Experimental)
import permission from './modules/permission'
import getters from './getters'
import goods from "@/store/modules/goods";

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    app,
    user,
    permission,
    goods,
    good,
    order
  },
  state: {},
  mutations: {},
  actions: {},
  getters
})
