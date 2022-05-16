<template>
  <a-form @submit='handleSubmit' :form='form'>
    <a-form-item
      label='商品类别名称'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['name', {rules:[{required: true, message: '请输入名称'}]}]" />
    </a-form-item>

    <a-form-item
      label='数量（库存）'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['num', {rules:[{required: true, message: '请输入数量（库存）'},{pattern:'^(([1-9]{1}\\d*)|(0{1}))$', message: '只允许输入整数'}]}]" />
    </a-form-item>

    <a-form-item
      label='价格'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['price', {rules:[{required: true, message: '请输入价格'}, {pattern:'^(([1-9]{1}\\d*))(\\.\\d{1,2})?$', message: '最多输入2位小数'}]}]" />
    </a-form-item>
  </a-form>
</template>

<script>
import pick from "lodash.pick";
import {mapActions} from "vuex";
import {addGoodsCategory} from "@/api/manage";

const fields = ["name", "num", "price"]

export default {
  name: "AddCategoryForm",
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
      console.log('监听了 modal ok 事件')

      const { form: { validateFields } } = this
      this.visible = true

      return new Promise(resolve => {
        validateFields((errors, values) => {
          if (!errors) {
            const params = { ...values }
            params.goodsId = this.$route.params.goodsId
            addGoodsCategory(params)
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

    }
  }
}
</script>

<style scoped>

</style>