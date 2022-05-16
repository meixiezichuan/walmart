<template>
  <a-form @submit='handleSubmit' :form='form'>
    <a-form-item
      label='名称'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['name', {rules:[{required: true, message: '请输入商品名称'}]}]" />
    </a-form-item>
    <a-form-item
      label='介绍'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-textarea v-decorator="['description']"/>
    </a-form-item>
    <a-form-item
      label='数量'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input-number min='0' v-decorator="['num', {rules:[{required: true, message: '请输入商品数量'}]}]" />
    </a-form-item>
    <a-form-item
      label='商户id'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-input v-decorator="['user_id', {rules:[{required: true, message: '请输入商品数量'}]}]" />
    </a-form-item>
    <a-form-item
      label='图片'
      :labelCol='labelCol'
      :wrapperCol='wrapperCol'
    >
      <a-upload
        action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
        list-type="picture-card"
        :file-list="fileList"
        @preview="handlePreview"
        @change="handleChange"
      >
        <div v-if="fileList.length < 1">
          <a-icon type="plus" />
          <div class="ant-upload-text">
            Upload
          </div>
        </div>
      </a-upload>
      <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
        <img alt="example" style="width: 100%" :src="previewImage" />
      </a-modal>
    </a-form-item>
  </a-form>
</template>

<script>
import pick from 'lodash.pick'

const fields = ['name', 'description', 'num', 'user_id', 'image']

function getBase64(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result);
    reader.onerror = error => reject(error);
  });
}

export default {
  name: 'TaskForm',
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
      previewVisible: false,
      previewImage: '',
      fileList: [],
    }
  },
  mounted() {
    if(this.record && this.record.image){
      this.fileList.push({
        uid: '-1',
        name: 'image.png',
        status: 'done',
        url: this.record.image,
      })
    }
    this.record && this.form.setFieldsValue(pick(this.record, fields))
  },
  methods: {
    onOk() {
      console.log('监听了 modal ok 事件')
      return new Promise(resolve => {
        resolve(true)
      })
    },
    onCancel() {
      console.log('监听了 modal cancel 事件')
      return new Promise(resolve => {
        resolve(true)
      })
    },
    handleSubmit() {
      const { form: { validateFields } } = this
      this.visible = true
      validateFields((errors, values) => {
        if (!errors) {
          console.log('values', values)
        }
      })
    },
    handleCancel() {
      this.previewVisible = false;
    },
    async handlePreview(file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj);
      }
      this.previewImage = file.url || file.preview;
      this.previewVisible = true;
    },
    handleChange({ fileList }) {
      this.fileList = fileList;
    },
  }
}
</script>
