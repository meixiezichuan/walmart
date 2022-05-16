<template>
  <div style='text-align: center'>
    <a-table
      :columns='columns'
      :pagination='false'
      :row-key='record => record.categoryId'
      :data-source='goods'
    >
      <span slot='pic' slot-scope='text, record'>
        <img style='width:50px;height:50px' :src=record.goodsImage alt='暂无图片'>
      </span>
      <span slot='amount' slot-scope='text, record'>
        {{ (record.num * record.price).toFixed(2)}}
      </span>
    </a-table>

    <p style='text-align: right'>
      合计：
      <span class='money'>{{ total_money.toFixed(2) }}</span> 元
    </p>

    <a-button-group>
      <a-button type='primary' @click='nextStep'>下一步</a-button>
      <a-button style='margin-left: 8px' @click='back'>返回购物车</a-button>
    </a-button-group>
  </div>
</template>

<script>

const columns = [
  {
    title: '商品基本信息',
    align: 'center',
    children: [
      {
        title: '商铺',
        dataIndex: 'storeName',
        align: 'center'
      },
      {
        title: '商品图片',
        dataIndex: 'goodsImage',
        align: 'center',
        scopedSlots: { customRender: 'pic' }
      },
      {
        title: '名称',
        dataIndex: 'goodsName',
        align: 'center'
      },
      // {
      //   title: '介绍',
      //   dataIndex: 'goods.description',
      //   align: 'center'
      // },
    ]
  },
  {
    title: '规格',
    align: 'center',
    children: [
      {
        title: '类别',
        dataIndex: 'categoryName',
        align: 'center'
      },
      {
        title: '单价',
        dataIndex: 'price',
        align: 'center'
      },
    ]
  },
  {
    title: '数量',
    dataIndex: 'num',
    align: 'center',
    scopedSlots: { customRender: 'num' }
  },
  {
    title: '金额',
    align: 'center',
    scopedSlots: { customRender: 'amount' }
  }
]

export default {
  name: 'CreateOrder',
  data() {
    return {
      columns,
      goods: [],
      total_money: 0
    }
  },
  mounted() {
    const param = this.$route.params.goods

    if (param === undefined) {
      this.$router.push({ path: '404' })
    } else {
      console.log(param)
      this.goods = param
      this.goods.forEach(item => {
        this.total_money += item.num * item.price
      })
    }
  },
  methods: {
    back() {
      this.$router.push({ name: 'shoppingCart' })
    },
    nextStep() {
      this.$emit('nextStep')
    }
  }
}
</script>

<style lang='less' scoped>
.money {
  font-family: "Helvetica Neue", sans-serif;
  font-weight: 500;
  font-size: 20px;
  color: red;
  line-height: 14px;
}
</style>