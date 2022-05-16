<template>
  <page-header-wrapper>
    <div id="wallet" style='text-align: center; margin-top: 80px; padding-bottom: 20px'>
      <a-avatar style="color:white; background-color: #e7db71" :size='100' icon='wallet'/>
    </div>
    <div id="balance" style="text-align: center; padding-bottom: 40px">
      <h1 style="font-size: 30px">我的余额</h1>
      <h2 style="font-size: 25px; padding-bottom: 20px" >¥{{this.money.toFixed(2)}}</h2>
      <a-button v-if='user.role===3' @click="recharge" id="recharge" type="primary" style="font-size: 25px; width: 110px; height: 44px; background-color: #52c41a; border-color: #52c41a" >
        充值
      </a-button>
    </div>
    <div id="transactionRecord">
      <a-card title="交易流水(最多显示10条记录）">
        <a-table>
<!--          :columns='columns'-->
<!--          :row-key='record => record.id'-->
<!--          :data-source='data'-->
<!--          :pagination='pagination'-->
<!--          :loading='loading'-->
<!--          @change='handleTableChange'-->

        </a-table>
      </a-card>
    </div>
  </page-header-wrapper>
</template>

<script>
import { STable } from '@/components'
import Recharge from "@/views/wallet/Recharge";
import storage from 'store'
import {getWallet} from "@/api/buyer";

export default {
  name: "WalletManage",
  components: {
    STable
  },
  data(){
    return {
      user: storage.get('user'),
      money: 0
    }
  },
  mounted() {
    this.fetch()
  },
  methods: {
    recharge() {
      let me = this
      this.$dialog(Recharge,
        // component props
        {
          record: {},
          on: {
            ok() {
              console.log('ok 回调')
              //console.log(record)
              //console.log('balance', balance)
              me.fetch()
            },
            cancel() {
              console.log('cancel 回调')
            },
            close() {
              console.log('modal close 回调')
            }
          }
        },
        // modal props
        {
          title: '充值',
          width: 500,
          centered: true,
          maskClosable: false
        })
    },
    fetch () {
      getWallet()
        .then((res) => {
          console.log('data', res)
          this.money = res.data.data.balance
        })
    }
  }

}
</script>

<style scoped>

</style>