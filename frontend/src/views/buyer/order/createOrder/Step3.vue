<template>
  <a-result
    status="success"
    title="下单成功!"
    sub-title="请点击下方按钮付款。"
  >
    <div class="information">
      <a-row>
        <a-col :sm="8" :xs="24">订单编号：</a-col>
        <a-col :sm="16" :xs="24">{{ order.id }}</a-col>
      </a-row>
      <a-row>
        <a-col :sm="8" :xs="24">总金额：</a-col>
        <a-col :sm="16" :xs="24"><span class="money">{{ order.totalPrice }}</span> 元</a-col>
      </a-row>
    </div>
    <template #extra>
      <a-button type="primary" @click='pay'>
        支付
      </a-button>
    </template>
  </a-result>
</template>

<script>
import {payOrder} from "@/api/order";

export default {
  name: "Step3",
  props: {
    order: Object
  },
  methods: {
    pay() {
      const that = this
      console.log(this.order.id)
      payOrder(this.order.id).then(res => {
        this.$notification.success({
          message: '支付成功',
          description: res.data.detail
        })

        that.timer = setTimeout(function () {
          that.loading = false
          that.$emit('nextStep')
        }, 1500)
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
  font-family: "Helvetica Neue", sans-serif;
  font-weight: 500;
  font-size: 20px;
  line-height: 14px;
}
</style>