<template>
  <a-form :form='form'>
    <a-form-item
      label='商品数量'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input-number :min='1' :max='record.goods.num'
                      v-decorator="['num', {rules:[{required: true, message: '请输入数量'}]}]" />
    </a-form-item>
  </a-form>
</template>

<script>
import pick from '_lodash.pick@4.4.0@lodash.pick'
import { changeGoodsNumInCart } from '@/api/shoppingCart'

const fields = ['num']

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
    onOk() {
      const { form: { validateFields } } = this
      this.visible = true
      return new Promise(resolve => {
        validateFields((errors, values) => {
          if (!errors) {
            console.log(values)
            changeGoodsNumInCart({
              'goods_id': this.record.goods.id,
              'num': values.num
            }).then((res) => {
              if (res.data.status === 409) {
                console.log("???")
              } else if (res.data.status === 404) {
                console.log("!!!")
              } else {
                this.$notification.success({
                  message: '修改成功',
                  description: res.data.detail
                })
              }
            }).catch(err => {
              console.log(err)
            }).finally(() => {
              resolve(888)
            })
          }
        })
      })
    },
    onCancel() {
      return new Promise(resolve => {
        resolve(true)
      })
    }
  }
}
</script>

<style scoped>

</style>