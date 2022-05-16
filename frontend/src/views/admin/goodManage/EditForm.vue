<template>
  <a-form @submit='handleSubmit' :form='form'>
    <a-form-item
      label='商品id'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['id']" :disabled='true' />
    </a-form-item>
    <a-form-item
      label='名称'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['name', {rules:[{required: true, message: '请输入名称'}]}]" />
    </a-form-item>

    <a-form-item
      label='描述'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['description']" />
    </a-form-item>

    <a-form-item
      label='图片'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['image']" />
    </a-form-item>

    <a-form-item
      label='数量'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input-number :min="0" v-decorator="['num', {rules:[{required: true, message: '请输入数量'}]}]" />
    </a-form-item>


    <a-form-item
      label='用户id'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['userId', {rules:[{required: true, message: '请输入用户id'}]}]" />
    </a-form-item>

    <a-form-item
      label='删除'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-select v-decorator="['deprecated', {initialValue:0 ,rules:[{required: true, message: '请选择是否删除'}]}]">
        <a-select-option :value="0">未删除</a-select-option>
        <a-select-option :value="1">已删除</a-select-option>
      </a-select>
    </a-form-item>
  </a-form>
</template>

<script>
import pick from 'lodash.pick'
import { mapActions } from 'vuex'

const fields = ['id', 'name', 'description', 'image', 'num', 'userId']

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
    ...mapActions(['UpdateGoodInfo']),
    onOk() {
      console.log('监听了 modal ok 事件')

      const { form: { validateFields }, UpdateGoodInfo } = this
      this.visible = true
      return new Promise(resolve => {
        validateFields((errors, values) => {
          if (!errors) {
            UpdateGoodInfo(values)
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
