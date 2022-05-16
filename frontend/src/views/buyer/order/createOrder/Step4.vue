<template>
  <a-result
    status="success"
    title="下单成功!"
    sub-title="商家会尽快为您发货。"
  >
    <template #extra>
      <a-button type="primary" @click='goOrderPage'>
        查看订单
      </a-button>
    </template>
  </a-result>
</template>

<script>
export default {
  name: 'Step4',
  data () {
    return {
      loading: false
    }
  },
  methods: {
    finish () {
      this.$emit('finish')
    },
    goOrderPage(){
      this.$router.push({
        path: '/buyer/order/orderManage'
      })
    }
  }
}
</script>
<style lang="less" scoped>
.information {
  line-height: 22px;

  .ant-row:not(:last-child) {
    margin-bottom: 24px;
  }
}
.money {
  font-family: "Helvetica Neue",sans-serif;
  font-weight: 500;
  font-size: 20px;
  line-height: 14px;
}
</style>
