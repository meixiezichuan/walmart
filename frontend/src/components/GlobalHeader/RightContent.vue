<template>
  <div :class='wrpCls'>
<!--    <a-badge count="5" v-if='this.user.role === 3'>-->
<!--      <a @click='jumpToCart'><a-icon style='font-size: 30px' type="shopping-cart" /></a>-->
<!--    </a-badge>-->
    <a-badge dot :count='count'>
      <a-icon type="notification" @click="jumpToNotification()"/>
    </a-badge>
    <avatar-dropdown :menu='showMenu' :current-user='currentUser' :class='prefixCls' />
  </div>
</template>

<script>
import AvatarDropdown from './AvatarDropdown'
import SelectLang from '@/components/SelectLang'
import { getUserByToken } from '@/api/buyer'
import storage from 'store'
import { ACCESS_TOKEN } from '@/store/mutation-types'
import { getNotificationList } from '@/api/notification'

export default {
  name: 'RightContent',
  components: {
    AvatarDropdown,
    SelectLang
  },
  props: {
    prefixCls: {
      type: String,
      default: 'ant-pro-global-header-index-action'
    },
    isMobile: {
      type: Boolean,
      default: () => false
    },
    topMenu: {
      type: Boolean,
      required: true
    },
    theme: {
      type: String,
      required: true
    },
  },
  data() {
    return {
      showMenu: true,
      currentUser: {},
      username: '',
      user: {},
      count: 0
    }
  },
  computed: {
    wrpCls() {
      return {
        'ant-pro-global-header-index-right': true,
        [`ant-pro-global-header-index-${(this.isMobile || !this.topMenu) ? 'light' : this.theme}`]: true
      }
    }
  },
  mounted() {
    this.user = storage.get('user')

    this.fetchNotification()

    setTimeout(() => {
      this.currentUser = {
        name: this.user.name
      }
    }, 1500)
  },

  methods: {
    jumpToCart(){
      this.$router.push({path : "/account/shoppingCart"})
    },
    fetchNotification(){
      getNotificationList()
        .then(res => {
          this.count = res.data.data.filter(item => item.isRead === 0).length
        })
    },
    jumpToNotification(){
      this.$router.push({path : "/account/notification"})
    }
  }
}
</script>
