<template>
  <page-header-wrapper>
    <a-steps :current=current style="margin-bottom: 20px">
      <a-step title="商品创建" description="基本信息" />
      <a-step title="商品分类创建" description="子分类" />
    </a-steps>
    <a-card>
    <div class="steps-content">
      <a-form v-if="current===0" :form='goodsForm'>
        <a-form-item
          label='商品名称'
          :labelCol='labelCol'
          :wrapperCol='wrapperCol'
        >
          <a-input v-decorator="['goodsName', {rules:[{required: true, message: '请输入名称'}]}]" />
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
<!--          <a-input v-decorator="['image']" />-->
<!--          <div id="ossfile">你的浏览器不支持flash,Silverlight或者HTML5！</div>-->

          <div id="container">
            <a-button id="selectfiles"><a-icon type="upload" /> 选择文件</a-button>
            <a-button id="postfiles" style="display: none">开始上传</a-button>
          </div>

          <img id="goodsImage" src="" style="display: none;"/>
<!--          <pre id="console"></pre>-->
        </a-form-item>
      </a-form>
      <a-form v-if="current===1" :form='categoryForm'>
        <a-form-item
          label='分类名称'
          :labelCol='labelCol'
          :wrapperCol='wrapperCol'
        >
          <a-input v-decorator="['categoryName', {rules:[{required: true, message: '请输入名称'}]}]" />
        </a-form-item>

        <a-form-item
          label='数量（库存）'
          :labelCol='labelCol'
          :wrapperCol='wrapperCol'
        >
          <a-input type="number" min="0" step="1" v-decorator="['num', {rules:[{required: true, message: '请输入数量'}, {pattern:'^(([1-9]{1}\\d*)|(0{1}))$', message: '只允许输入整数'}]}]" />
        </a-form-item>

        <a-form-item
          label='价格'
          :labelCol='labelCol'
          :wrapperCol='wrapperCol'
        >
          <a-input type="number" min="0.01" step="0.01" v-decorator="['price', {rules:[{required:true, message: '请输入价格'}, {pattern:'^(([1-9]{1}\\d*))(\\.\\d{1,2})?$', message: '最多输入2位小数'}]}]" />
        </a-form-item>
      </a-form>
    </div>
    </a-card>
    <div class="steps-action" style="text-align: center">
      <a-button v-if="current < 1" type="primary" @click="next">下一步</a-button>
      <a-button
        v-if="current === 1"
        type="primary"
        @click="onOk"
      >
        完成
      </a-button>
      <a-button v-if="current > 0" style="margin-left: 8px" @click="prev">上一步</a-button>
    </div>
  </page-header-wrapper>
</template>

<script>
import pick from 'lodash.pick'
import { mapActions } from 'vuex'
import storage from 'store'
import { ACCESS_TOKEN } from '@/store/mutation-types'
import {insertGoodsCategory} from "@/api/manage";
import {initPlUploader} from "@/utils/upload";

const fields = ['id', 'name', 'description', 'image', 'num', 'userId']

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
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 13 }
      },
      goodsForm: this.$form.createForm(this),
      categoryForm: this.$form.createForm(this),
      current: 0,
      data: {},
      goodsName: "",
      storeId : this.$route.params.storeId
    }
  },
  mounted () {
    initPlUploader()

    this.record && this.goodsForm.setFieldsValue(pick(this.record, fields)) && this.categoryForm.setFieldsValue(pick(this.record, fields))
  },
  methods: {
    ...mapActions(['insertGood']),

    onOk() {
      console.log('监听了 modal ok 事件')

      const { categoryForm: { validateFields }, insertGood } = this
      this.visible = true

      return new Promise(resolve => {
        validateFields((errors, values) => {
          if (!errors) {
            let params = {...values}
            //params.userId = storage.get('user').id
            this.data.name = this.data.goodsName
            this.data.storeId = this.storeId

            this.data.image = storage.get("imageUrl")

            console.log(this.data)

            insertGood(this.data)
              .then((res) => {
                this.$notification.success({
                  message: '新增商品成功',
                  description: '新增商品成功',
                  duration: 4
                })
                resolve(true)
                console.log(res)
                params.goodsId = res.data.id
                params.name = params.categoryName
                Object.assign(this.data, params)
                insertGoodsCategory(params)
                  .then((res) => {
                    this.$notification.success({
                      message: '新增商品类别成功',
                      description: '新增商品类别成功',
                      duration: 4
                    })
                    resolve(true)
                    console.log(res)
                    this.$router.push({
                      name: 'goodManage'
                    })
                  })
              })
          }
        })
      })
    },

    next () {
      const { goodsForm: { validateFields }} = this
      validateFields((errors, values) => {
        if (!errors) {
          let params = {...values}
          params.userId = storage.get('user').id
          this.data = params
          console.log(this.data)
          console.log(storage.get(ACCESS_TOKEN))
          this.current++;
        }
      })

    },

    prev () {
      this.current--;
      console.log(this.data)
      console.log(pick(this.data, ['goodsName', 'image', 'description']))
      this.goodsForm.setFieldsValue(pick(this.data, ['goodsName', 'image', 'description']))
    }

  }
}
</script>
<style scoped>
  .steps-action {
    margin-top: 24px;
  }
</style>