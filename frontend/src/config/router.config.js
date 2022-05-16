// eslint-disable-next-line
import {UserLayout, BasicLayout, BlankLayout} from '@/layouts'
import {bxAnaalyse} from '@/core/icons'

const RouteView = {
  name: 'RouteView',
  render: h => h('router-view')
}

export const asyncRouterMap = [
  {
    path: '/',
    name: 'index',
    component: BasicLayout,
    meta: {title: 'menu.home'},
    // redirect: '/admin/userManage',
    children: [
      /* admin */
      {
        path: '/admin',
        name: 'admin',
        redirect: '/admin/userManage',
        component: RouteView,
        meta: {title: 'menu.admin', keepAlive: true, icon: bxAnaalyse, permission: [1]},
        children: [
          {
            path: '/admin/userManage',
            name: 'userManage',
            component: () => import('@/views/admin/userManage/UserManage'),
            meta: {title: 'menu.admin.userManage', keepAlive: true, permission: [1]}
          },
          {
            path: '/admin/goodManage',
            name: 'adminGoodManage',
            component: () => import('@/views/admin/goodManage/GoodManage'),
            meta: {title: 'menu.admin.goodManage', keepAlive: true, permission: [1]}
          },
          {
            path: '/admin/complaintManage',
            name: 'complaintManage',
            component: () => import('@/views/admin/ComplaintManage'),
            meta: {title: 'menu.admin.complaintManage', keepAlive: true, permission: [1]}
          }
        ]
      },

      /* seller */
      {
        path: '/seller',
        name: 'seller',
        redirect: '/seller/goodManage',
        component: RouteView,
        meta: {title: '商家管理', keepAlive: true, icon: bxAnaalyse, permission: [2]},
        children: [
          {
            path: '/seller/goodManage',
            name: 'sellerGoodManage',
            component: () => import('@/views/seller/manage/goodManage/GoodManage'),
            meta: {title: '商品管理', keepAlive: true, permission: [2]}
          },
          {
            path: '/seller/storeManage',
            name: 'storeManage',
            component: () => import('@/views/seller/manage/storeManage/AddStore'),
            meta: {title: '商铺管理', keepAlive: true, permission: [2]}
          },
          {
            path: '/seller/orderManage',
            name: 'sellerOrderManage',
            component: () => import('@/views/seller/order/OrderManage'),
            meta: {title: '订单管理', keepAlive: true, permission: [2]}
          },
          {
            path: '/seller/category',
            name: 'categoryManage',
            component: () => import('@/views/seller/manage/goodManage/category/CategoryManager'),
            meta: {title: '商品分类管理', keepAlive: true, permission: [2]},
            hidden: true
          },
          {
            path: '/seller/goods/add',
            name: 'addGoods',
            component: () => import('@/views/seller/manage/goodManage/AddForm'),
            meta: {title: '商品添加', keepAlive: true, permission: [2]},
            hidden: true
          }
        ]
      },

      /* buyer */
      {
        path: '/buyer/goods_list',
        name: 'goods_list',
        component: () => import('@/views/buyer/goods/goods_list/GoodsList'),
        meta: {title: 'menu.goods.goodsList', icon: 'shop', hideHeader: true, permission: [3]}
      },

      {
        path: '/buyer/createOrder',
        name: 'createOrder',
        hidden: true,
        component: () => import('@/views/buyer/order/createOrder/CreateOrder'),
        meta: {title: '创建订单', hideHeader: true, permission: [3]}
      },

      {
        path: '/buyer/order/orderManage',
        name: 'buyerOrderManage',
        component: () => import('@/views/buyer/order/orderManage/OrderManage'),
        meta: {title: '订单管理', icon: 'profile', hideHeader: true, permission: [3]}
      },

      /* account */
      {
        path: '/account',
        component: RouteView,
        redirect: '/account/personal_info',
        name: 'account',
        meta: {title: 'menu.personal', icon: 'user', keepAlive: true, permission: [1, 2, 3]},
        children: [
          {
            path: '/account/personal_info',
            name: 'personal_info',
            component: () => import('@/views/account/PersonalInfo'),
            meta: {title: 'menu.personal.personalInfo', icon: 'user', permission: [2, 3]}
          },
          {
            path: '/account/wallet',
            name: 'wallet',
            component: () => import('@/views/wallet/WalletManage'),
            meta: {title: '我的钱包', keepAlive: true, icon: 'wallet', permission: [1, 2, 3]}
          },
          {
            path: '/account/shoppingCart',
            name: 'shoppingCart',
            component: () => import('@/views/buyer/shoppingCart/ShoppingCart'),
            meta: {title: '购物车', icon: 'shopping-cart', permission: [3]}
          },
          {
            path: '/account/notification',
            name: 'notification',
            component: () => import('@/views/notification/Notification'),
            meta: {title: '我的消息', icon: 'notification', permission: [2, 3]}
          },
        ]
      },
    ]
  },
  {
    path: '*',
    redirect: '/404',
    hidden: true
  }
]
/**
 * 基础路由
 * @type { *[] }
 */
export const constantRouterMap = [
  {
    path: '/user',
    component: UserLayout,
    redirect: '/user/login',
    hidden: true,
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/Login')
      },
      {
        path: 'register',
        name: 'register',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/Register')
      },
      {
        path: 'register-result',
        name: 'registerResult',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/RegisterResult')
      },
      {
        path: 'recover',
        name: 'recover',
        component: undefined
      }
    ]
  },

  {
    path: '/404',
    component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/404')
  }
]
