<template>
  <a-form @submit='handleSubmit' :form='form'>
    <a-form-item
        label='id'
        :labelCol='labelCol'
        :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['id']" :disabled='true' />
    </a-form-item>

    <a-form-item
        label='配送地址'
        :labelCol='labelCol'
        :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['consignInfo', {rules:[{required: true, message: '请输入配送地址'}]}]" />
    </a-form-item>

    <a-form-item
        label='状态'
        :labelCol='labelCol'
        :wrapperCol='wrapperCol'
    >
      <a-select v-decorator="['status']" :disabled='true'>
        <a-select-option :value="2">待付款</a-select-option>
        <a-select-option :value="3">待发货</a-select-option>
        <a-select-option :value="4">待收货</a-select-option>
        <a-select-option :value="1">已完成</a-select-option>
        <a-select-option :value="5">已取消</a-select-option>
      </a-select>
    </a-form-item>
  </a-form>
</template>

<script>
import pick from 'lodash.pick'
import { mapActions } from 'vuex'
import {updateOrderInfo} from "@/api/order";

const fields = ['id', 'consignInfo', 'status']

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
      form: this.$form.createForm(this),
    }
  },
  mounted() {
    this.record && this.form.setFieldsValue(pick(this.record, fields))
  },
  methods: {
    ...mapActions(['updateOrderInfo']),
    onOk() {
      console.log('监听了 modal ok 事件')

      const { form: { validateFields }, updateOrderInfo } = this
      this.visible = true
      return new Promise(resolve => {
        validateFields((errors, values) => {
          if (!errors) {
            updateOrderInfo(values)
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
