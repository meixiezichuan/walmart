<template>
  <div>
    <div style='text-align: center; margin-top: 20px; padding-bottom: 10px'>
      <a-avatar shape='square' :src='record.image?record.image:imgData' :size='100'/>
    </div>

    <a-form :form="form">
      <a-form-item
        label='规格'
        :labelCol='labelCol'
        :wrapperCol='wrapperCol'>
        <a-select style="width: 100%" placeholder="请选择规格" @change="handleCategoryChange"
                  v-decorator="['categoryId', {rules:[{required:true, message: '请选择规格'}]}]">
          <a-select-option v-for="d in data" :key="d.id">
            <span style="width: 100%">{{ d.name + ', ' }}<b style="text-align: right">{{ '¥' + d.price }}</b></span>
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item
        :label='label'
        :labelCol='labelCol'
        :wrapperCol='wrapperCol'>
        <a-input-number
          id="inputNumber"
          :disabled="num === 0"
          :min="1"
          :max="num"
          v-decorator="['inputNumber', {rules:[{required:true, message: '请输入购买数量'}]}]"/>
      </a-form-item>
    </a-form>

<!--    <div style="text-align: center;">-->
<!--      <div style="text-align: left; margin-bottom: 20px">-->
<!--        <a-radio-group v-for="d in data" v-model:value="value" :defaultValue="data[0].id">-->
<!--          <a-radio-button :value='d.id' style="margin-right: 5px; margin-bottom: 5px" @click="click">{{ d.name }}-->
<!--          </a-radio-button>-->
<!--        </a-radio-group>-->
<!--      </div>-->
<!--      <div style="text-align: left">-->
<!--        <p v-model:num="num" v-model:price="price" style="white-space: pre; font-size: large">-->
<!--          <b>购买数量 （库存：{{ num }})</b>-->
<!--          <a-form :form="form">-->
<!--            <a-form-item>-->
<!--              <a-input-number-->
<!--                style="margin-left: 20px"-->
<!--                id="inputNumber"-->
<!--                :defaultValue="1"-->
<!--                :min="1"-->
<!--                :max="num"-->
<!--                v-decorator="['inputNumber', {rules:[{required:true, message: '请输入购买数量'}]}]"/>-->
<!--            </a-form-item>-->
<!--          </a-form>-->
<!--          {{ '\n' }} <b>价格： </b>{{ price === 0 ? '请先选择商品类别' : price.toFixed(2) }}-->
<!--        </p>-->
<!--      </div>-->
<!--    </div>-->
  </div>
</template>

<script>
import {AntDesignOutlined} from '@ant-design/icons-vue';
import {getGoodsCategory} from "@/api/manage";
import {addGoodsToCart} from "@/api/shoppingCart";

export default {
  name: "GoodsCart.vue",
  props: {
    record: {
      type: Object,
      default: null
    },
    isCart: Boolean
  },
  data() {
    return {
      imgData: 'https://gitee.com/cuiyunna/walmart-image/raw/master/goods.png',
      data: [],
      value: "",
      num: 0,
      price: 0,
      name: "",
      label: "数量",

      labelCol: {
        xs: {span: 24},
        sm: {span: 7}
      },
      wrapperCol: {
        xs: {span: 24},
        sm: {span: 13}
      },
      form: this.$form.createForm(this)
    }
  },
  mounted() {
    this.record && this.getGoodsCategory()
  },
  components: {
    AntDesignOutlined,
  },
  methods: {
    getGoodsCategory() {
      let getParam = {"goodsId": this.record.id}
      getGoodsCategory(getParam)
        .then((res) => {
          console.log(res)
          this.data = res.data.data.categories
          console.log('data', this.data)
          for (let i = 0; i < this.data.length; i++) {
            this.num += this.data[i].num
          }
          console.log('num', this.num)
        })
    },

    handleCategoryChange(value) {
      for (let i = 0; i < this.data.length; i++) {
        if (value === this.data[i].id) {
          this.value = value
          this.num = this.data[i].num
          this.price = this.data[i].price
          this.name = this.data[i].name

          this.label = '数量(库存' + this.num + '件)'
          break
        }
      }
    },

    // click(e) {
    //   for (let i = 0; i < this.data.length; i++) {
    //     if (e.target.value === this.data[i].id) {
    //       this.num = this.data[i].num
    //       this.price = this.data[i].price
    //       this.name = this.data[i].name
    //       break
    //     }
    //   }
    // },
    onOk() {
      const {form: {validateFields}} = this

      if (this.isCart) {
        return new Promise(resolve => {
          validateFields((errors, values) => {
            if (!errors) {
              addGoodsToCart({
                categoryId: this.value,
                num: values.inputNumber
              }).then(res => {
                this.$notification.success({
                  message: '添加购物车成功',
                  description: res.data.detail,
                  duration: 4
                })
                resolve(true)
              })
            }
          })
        })
      } else {
        return new Promise(resolve => {
          validateFields((errors, values) => {
            if (!errors) {
              console.log(this.record)
              let goods = this.record
              goods['goodsName'] = goods.name
              goods['goodsImage'] = goods.image
              goods['categoryId'] = this.value
              goods['categoryName'] = this.name
              goods['num'] = values.inputNumber
              goods['price'] = this.price

              console.log("goods")
              console.log(goods)

              this.$router.push({
                name: 'createOrder',
                params: {
                  goods: [goods],
                  isFromCart: false
                }
              })

              resolve(true)
            }
          })
        })
      }
    }
  }
}
</script>

<style scoped>

</style>