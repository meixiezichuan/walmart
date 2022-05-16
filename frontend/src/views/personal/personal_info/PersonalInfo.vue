<template>
  <div class='box'>
    <div style='text-align: center; margin-top: 80px; padding-bottom: 40px'>
      <a-avatar shape='square' :size='100' icon='user'/>
    </div>
    <div>
      <a-col flex='1 1 200px'>
        <div style='width: 300px'>
          <a-row style='margin-top: 10px' type='flex'>
            <a-col flex='1 1 60px'>用户名</a-col>
            <a-col flex='0 1 240px'>
              <a-input v-model='name' placeholder='用户名'/>
            </a-col>
          </a-row>
          <a-row style='margin-top: 10px' type='flex'>
            <a-col flex='1 1 60px'>密码</a-col>
            <a-col flex='0 1 240px'>
              <a-input-password v-model='pwd' placeholder='密码'/>
            </a-col>
          </a-row>
          <a-row style='margin-top: 10px' type='flex'>
            <a-col flex='1 1 60px'>邮箱</a-col>
            <a-col flex='0 1 240px'>
              <a-input v-model='email' placeholder='邮箱'/>
            </a-col>
          </a-row>
          <a-row style='margin-top: 10px' type='flex'>
            <a-col flex='1 1 60px'>电话</a-col>
            <a-col flex='0 1 240px'>
              <a-input v-model='phone_num' placeholder='电话'/>
            </a-col>
          </a-row>
          <a-row style='margin-top: 10px' type='flex'>
            <a-col flex='1 1 60px'> 地址</a-col>
            <a-col flex='0 1 240px'>
              <div>
                <a-textarea v-model='address' placeholder='地址' auto-size/>
                <div style='margin: 24px 0'/>
              </div>
            </a-col>
          </a-row>
        </div>
      </a-col>
      <div style='position:absolute; right: 0; margin-right: 20px'>
        <a-button type='primary' @click="updateUser">
          保存信息
        </a-button>
      </div>
    </div>
  </div>
</template>
<script>
import storage from 'store'
import {ACCESS_TOKEN} from '@/store/mutation-types'
import {getUserByToken, updateUserInfo} from "@/api/buyer";

export default {
  name: 'PersonalInfo',
  data() {
    return {
      name: '',
      pwd: null,
      phone_num: null,
      email: null,
      address: null
    }
  },
  mounted() {
    this.$nextTick(function () {
      this.getUserInfo()
    })
  },
  methods: {
    getUserInfo() {
      const parameter = {
        token: storage.get(ACCESS_TOKEN)
      }
      getUserByToken(parameter).then(response => {
        console.log(response)
        this.name = response.data.data.name
        this.pwd = response.data.data.pwd
        this.phone_num = response.data.data.phone_num
        this.email = response.data.data.email
        this.address = response.data.data.address
      })
    },
    updateUser() {
      // console.log(storage.get('user_id'))
      const data = {
        id: storage.get('user_id'),
        name: this.name,
        pwd: this.pwd,
        phone_num: this.phone_num,
        email: this.email,
        address: this.address,
        role: storage.get('roleId') === 'buyer' ? 2 : 3,
      }
      // console.log(data)
      updateUserInfo(data).then(response => {
        this.$notification.success({
          message: '修改成功',
          description: response.data.detail
        })
        console.log(response)
      })
    }
  }
}
</script>
<style scoped>
.box {
  width: 300px;
  height: 500px;
  margin: auto;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}
</style>