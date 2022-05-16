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
      label='角色'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-select v-decorator="['role', {rules:[{required: true, message: '请选择用户角色'}]}]">
        <a-select-option :value="2">商家</a-select-option>
        <a-select-option :value="3">买家</a-select-option>
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

const fields = ['id', 'name', 'role', 'email', 'phone_num', 'address']

export default {
  name: 'EditForm',
  props: {
    record: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
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
    ...mapActions(['UpdateUserInfo']),
    onOk() {
      console.log('监听了 modal ok 事件')

      const { form: { validateFields }, UpdateUserInfo } = this
      this.visible = true
      return new Promise(resolve => {
        validateFields((errors, values) => {
          if (!errors) {
            UpdateUserInfo(values)
              .then((res) => {
                this.$notification.success({
                  message: '修改成功',
                  description: `修改成功`
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

    }
  }
}
</script>
