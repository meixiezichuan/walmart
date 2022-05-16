<template>
  <a-form @submit='handleSubmit' :form='form'>
    <a-form-item
      label='用户id'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['id']" :disabled='true' />
    </a-form-item>
    <a-form-item
      label='用户名'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['name', {rules:[{required: true, message: '请输入用户名'}]}]" />
    </a-form-item>

    <a-form-item
      label='密码'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'>
      <a-input-password
        size="large"
        placeholder="密码"
        @click="handlePasswordInputClick"
        v-decorator="['pwd', {rules: [{ required: true, message: '请输入密码' }, { validator: this.handlePasswordLevel }], validateTrigger: ['change', 'blur']}]"
      ></a-input-password>
    </a-form-item>

    <a-form-item
      label='确认密码'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'>
      <a-input-password
        size="large"
        placeholder="密码"
        v-decorator="['password2', {rules: [{ required: true, message: '请输入密码' }, { validator: this.handlePasswordCheck }], validateTrigger: ['change', 'blur']}]"
      ></a-input-password>
    </a-form-item>

    <a-form-item
      label='角色'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-select v-decorator="['role', {rules:[{required: true, message: '请选择用户角色'}]}]">
        <a-select-option :value="3">买家</a-select-option>
        <a-select-option :value="2">商家</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item
      label='邮箱'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['email',
      {rules: [{ type: 'email', message: '输入必须符合邮箱格式'}]}]" />
    </a-form-item>
    <a-form-item
      label='电话'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['phone_num',
      {rules: [{ pattern: '^1\\d{10}$', message: '输入必须符合电话号码格式'}]}]" />
    </a-form-item>
    <a-form-item
      label='地址'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['address']" />
    </a-form-item>
  </a-form>
</template>

<script>
import pick from 'lodash.pick'
import { mapActions } from 'vuex'
import { scorePassword } from '@/utils/util'
import md5 from 'md5'

const levelNames = {
  0: 'user.password.strength.short',
  1: 'user.password.strength.low',
  2: 'user.password.strength.medium',
  3: 'user.password.strength.strong'
}
const levelClass = {
  0: 'error',
  1: 'error',
  2: 'warning',
  3: 'success'
}
const levelColor = {
  0: '#ff0000',
  1: '#ff0000',
  2: '#ff7e05',
  3: '#52c41a'
}

const fields = ['id', 'name', 'role', 'email', 'phone_num', 'address']

export default {
  name: 'AddForm',
  props: {
    record: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      state: {
        time: 60,
        level: 0,
        smsSendBtn: false,
        passwordLevel: 0,
        passwordLevelChecked: false,
        percent: 10,
        progressColor: '#FF0000'
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 13 }
      },
      form: this.$form.createForm(this)
    }
  },
  mounted() {
    this.record && this.form.setFieldsValue(pick(this.record, fields))
  },
  methods: {
    ...mapActions(['insertUser']),
    onOk() {
      console.log('监听了 modal ok 事件')

      const { form: { validateFields }, insertUser } = this
      this.visible = true

      return new Promise(resolve => {
        validateFields((errors, values) => {
          if (!errors) {
            const params = { ...values }
            delete params.password2
            params.pwd = md5(values.pwd)
            insertUser(params)
              .then((res) => {
                this.$notification.success({
                  message: '新增成功',
                  description: '新增成功',
                  duration: 4
                })
                resolve(true)
              })
          }
        })
      })
    },
    onCancel() {
      console.log('监听了 modal cancel 事件')
      return new Promise(resolve => {
        resolve(true)
      })
    },
    handleSubmit() {

    },
    handlePasswordLevel (rule, value, callback) {
      if (value === '') {
        return callback()
      }
      console.log('scorePassword ; ', scorePassword(value))
      if (value.length >= 6) {
        if (scorePassword(value) >= 30) {
          this.state.level = 1
        }
        if (scorePassword(value) >= 60) {
          this.state.level = 2
        }
        if (scorePassword(value) >= 80) {
          this.state.level = 3
        }
      } else {
        this.state.level = 0
        callback(new Error('密码强度不够'))
      }
      this.state.passwordLevel = this.state.level
      this.state.percent = this.state.level * 33

      callback()
    },

    handlePasswordCheck (rule, value, callback) {
      const password = this.form.getFieldValue('pwd')
      // console.log('value', value)
      if (value === undefined) {
        callback(new Error('请输入密码'))
      }
      if (value && password && value.trim() !== password.trim()) {
        callback(new Error('请再次输入密码'))
      }
      callback()
    },

    handlePasswordInputClick () {
      if (!this.isMobile) {
        this.state.passwordLevelChecked = true
        return
      }
      this.state.passwordLevelChecked = false
    },
  }
}
</script>