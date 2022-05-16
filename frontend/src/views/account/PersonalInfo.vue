<template>
  <page-header-wrapper>
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
                <a-input v-model='user.name' placeholder='用户名'/>
              </a-col>
            </a-row>
            <a-row style='margin-top: 10px' type='flex'>
              <a-col flex='1 1 60px'>密码</a-col>
              <a-col flex='0 1 240px'>
                <a-input-password v-model='user.pwd' placeholder='密码'/>
              </a-col>
            </a-row>
            <a-row style='margin-top: 10px' type='flex'>
              <a-col flex='1 1 60px'>邮箱</a-col>
              <a-col flex='0 1 240px'>
                <a-input v-model='user.email' placeholder='邮箱'/>
              </a-col>
            </a-row>
            <a-row style='margin-top: 10px' type='flex'>
              <a-col flex='1 1 60px'>电话</a-col>
              <a-col flex='0 1 240px'>
                <a-input v-model='user.phone_num' placeholder='电话'/>
              </a-col>
            </a-row>
            <a-row style='margin-top: 10px' type='flex'>
              <a-col flex='1 1 60px'> 地址</a-col>
              <a-col flex='0 1 240px'>
                <div>
                  <a-textarea v-model='user.address' placeholder='地址' auto-size/>
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
  </page-header-wrapper>
</template>
<script>
import storage from 'store'
import {updateUserInfo} from "@/api/buyer";

export default {
  name: 'PersonalInfo',
  data() {
    return {
      // name: '',
      // pwd: null,
      // phone_num: null,
      // email: null,
      // address: null,
      user: {}
    }
  },
  mounted() {
    this.user = storage.get('user')
  },
  methods: {
    updateUser() {
      const data = {
        // id: storage.get('user').id,
        // name: this.name,
        // pwd: this.pwd,
        // phone_num: this.phone_num,
        // email: this.email,
        // address: this.address,
        // role: storage.get('user').role === 'buyer' ? 2 : 3,
        ...this.user,
      }
      // console.log(data)
      updateUserInfo(data).then(response => {
        this.$notification.success({
          message: '修改成功',
          description: response.data.detail
        })
        storage.set('user', data)
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