<template>
  <page-header-wrapper>
    <a-form :form='form' style="width: 500px">
      <a-form-item
        label='商铺名称'>
        <a-input v-decorator="['name', {rules:[{required: true, message: '请输入名称'}]}]" />
      </a-form-item>

      <a-form-item
        label='商铺描述'
      >
        <a-input v-decorator="['description']" />
      </a-form-item>

      <a-form-item>
        <a-button style="width: 100%" type="primary" @click='handleSubmit'>
          提交
        </a-button>
      </a-form-item>
    </a-form>
  </page-header-wrapper>
</template>

<script>
import { addStore } from '@/api/store'
import storage from 'store'

export default {
  name: "AddStore",
  data(){
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
  methods: {
    handleSubmit(){

      const { form: { validateFields } } = this

      validateFields((errors, values) => {
        if (!errors) {
          console.log(values)

          addStore({
            ...values,
            userId: storage.get('user').id
          }).then(res => {
            this.$notification.success({
              message: '新增成功',
              description: res.data.detail,
              duration: 4
            })
          })
        }
      })
    }
  }
}
</script>

<style scoped>

</style>