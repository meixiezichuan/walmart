<template>
  <a-form :form='form'>
    <a-form-item
      label='充值金额'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input id="money" prefix="￥"  placeholder="大于0" type="number"
               v-decorator="['money', {rules:[{required: true, message: '请输入金额'},{validator: check, message: '输入金额必须大于0'}, , {pattern:'^(([1-9]{1}\\d*))(\\.\\d{1,2})?$', message: '最多输入2位小数'}]}]" />
    </a-form-item>
  </a-form>
</template>

<script>
import pick from "lodash.pick";
import storage from "store";
import {updateWallet} from "@/api/buyer";
const fields = ['money']

export default {
  name: "Recharge",
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
      user: storage.get('user')
    }
  },
  mounted() {
    this.record && this.form.setFieldsValue(pick(this.record, fields))
  },
  methods:{
    check() {
      let x = document.getElementById("money").value
      //console.log(x)
      if (x <= 0) {
        return false
      }
      return true
    },
    onOk(){
      const { form: { validateFields } } = this
      return new Promise( resolve => {
        validateFields((errors, values) =>{
          if(!errors){
            //console.log(values)
            updateWallet({
              'type': 1,
              'amount': values.money,
              'userId': this.user.id
            }).then((res) => {
              console.log(res)
              // this.record.balance = res.data.data
              //balance = res.data.data
              resolve(true)
              this.$notification.success({
                message: '充值成功',
                description: res.data.detail
              })
            }).catch(err=>{
              console.log(err)
            })
          }
        })
      })
    }
  }
}
</script>

<style scoped>

</style>