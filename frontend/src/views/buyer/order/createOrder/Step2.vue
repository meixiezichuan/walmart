<template>
  <div style='text-align: center'>
    <a-form :form='form'>
      <a-form-item
        label='收货人姓名'
        :labelCol='labelCol'
        :wrapperCol='wrapperCol'
      >
        <a-input v-decorator="['name', {rules:[{required: true, message: '请输入收货人'}]}]"/>
      </a-form-item>

      <a-form-item
        label='手机号码'
        :labelCol='labelCol'
        :wrapperCol='wrapperCol'
      >
        <a-input
          v-decorator="['phoneNum', {rules: [{ required: true, message: '请输入手机号码' }, { pattern: /^1[3456789]\d{9}$/, message: $t('user.phone-number.wrong-format') }], validateTrigger: ['change', 'blur']}]"/>
      </a-form-item>

      <a-form-item
        label='收货地址'
        :labelCol='labelCol'
        :wrapperCol='wrapperCol'
      >
        <a-input v-decorator="['consignInfo', {rules:[{required: true, message: '请输入收货地址'}]}]"/>
      </a-form-item>
    </a-form>
    <a-button-group>
      <a-button :loading='loading' type='primary' @click='nextStep'>下单</a-button>
      <a-button style='margin-left: 8px' @click='prevStep'>上一步</a-button>
    </a-button-group>
  </div>
</template>

<script>
import {createOrder} from '@/api/order'
import {removeGoodsFromCart} from "@/api/shoppingCart";

export default {
  name: 'Step2',
  data() {
    return {
      labelCol: {
        xs: {span: 24},
        sm: {span: 7}
      },
      wrapperCol: {
        xs: {span: 24},
        sm: {span: 13}
      },
      loading: false,
      form: this.$form.createForm(this),
      goods: [],
      isFromCart: true
    }
  },
  mounted() {
    this.goods = this.$route.params.goods
    this.isFromCart = this.$route.params.isFromCart
  },
  methods: {
    prevStep() {
      this.$emit('prevStep')
    },
    nextStep() {
      const that = this
      const {form: {validateFields}} = this
      that.loading = true
      validateFields((err, values) => {
        if (!err) {
          let orderItems = {}
          this.goods.forEach(item => {
            orderItems[item.categoryId] = item.num
          })

          createOrder({
            orderItems: orderItems,
            address: values
          }).then(res => {
            const data = res.data
            this.$notification.success({
              message: '下单成功',
              description: data.detail
            })

            let categoryList = []
            this.goods.forEach(item => {
              categoryList.push(item.categoryId)
            })

            //从购物车中移除该商品
            if(this.isFromCart){
              removeGoodsFromCart({
                'categoryList': categoryList
              })
            }

            that.timer = setTimeout(function () {
              that.loading = false
              that.$emit('nextStep', data.data)
            }, 1500)
          }).catch(err => {
            console.log(err.response)
          }).finally(() => {

          })
        } else {
          that.loading = false
        }
      })
    },
  }
}
</script>

<style scoped>

</style>