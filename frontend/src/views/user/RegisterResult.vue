<template>
  <a-result
    :isSuccess="true"
    :content="false"
    :title="name"
    :sub-title="description">

    <template #extra>
      <a-button size="large" style="margin-left: 8px" @click="goHomeHandle">返回首页</a-button>
    </template>

  </a-result>
</template>

<script>
export default {
  name: 'RegisterResult',
  data () {
    return {
      description: '点击下方按钮返回登录页面。',
      form: {}
    }
  },
  computed: {
    name () {
      const v = this.form && this.form.name || 'xxx'
      return `你的账户：${v} 注册成功`
    }
  },
  created () {
    this.form = this.$route.params
  },
  methods: {
    goHomeHandle () {
      this.$router.push({ name: 'login' })
    }
  }
}
</script>

<style scoped>

</style>
