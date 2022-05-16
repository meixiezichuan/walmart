<template>
  <page-header-wrapper>
    <a-card
      style='margin-top: 24px'
      :bordered='false'>
      <a-tabs default-active-key='1'>
        <a-tab-pane key='1' tab='有效商品'>
          <a-table
            bordered
            :columns='columns'
            :row-key='record => record.categoryId'
            :data-source='data.filter(item => item.num !== 0)'
            :pagination='pagination'
            :row-selection='rowSelection'
            :loading='loading'
            @change='handleTableChange'
          >
            <span slot='pic' slot-scope='text, record'>
              <img style='width:50px;height:50px' :src=record.goodsImage alt='暂无图片'>
            </span>

            <template slot="num" slot-scope="text, record">
              <editable-cell :record="record" @change="OnNumChange(record.categoryId, 'num', $event)" />
            </template>

            <span slot='amount' slot-scope='text, record'>
              {{ (record.num * record.price).toFixed(2) }}
            </span>

            <span slot='action' slot-scope='text, record'>
              <template>
                <a @click='removeFromCart(record)'>移除</a>
              </template>
            </span>

          </a-table>
          <a-row>
            <a-col :span='12'>
              <a-button @click='removeManyFromCart'>批量移除</a-button>
            </a-col>
            <a-col :span='12'>
              <p style='text-align: right'>
                合计：
                <span class='money'>{{ total_money.toFixed(2) }}</span> 元
                <a-button :disabled='total_money === 0' type='primary' size='large' style='margin-left: 10px'
                          @click='buy'>
                  创建订单
                </a-button>
              </p>
            </a-col>
          </a-row>
        </a-tab-pane>
        <a-tab-pane key='2' tab='无效商品' force-render>
          <a-table
            :columns='invalid_columns'
            :row-key='record => record.categoryId'
            :data-source='data.filter(item => item.num === 0)'
            :pagination='pagination'
            :row-selection='rowSelection'
            :loading='loading'
            @change='handleTableChange'
          >
        <span slot='pic' slot-scope='text, record'>
          <img style='width:50px;height:50px' :src=record.goodsImage alt='暂无图片'>
        </span>

            <span slot='amount' slot-scope='text, record'>
          {{ record.num * record.price }}
        </span>

            <span slot='action' slot-scope='text, record'>
          <template>
            <a @click='removeFromCart(record)'>移除</a>
          </template>
        </span>
          </a-table>
          <a-row>
            <a-col :span='12'>
              <a-button @click='removeManyFromCart'>批量移除</a-button>
            </a-col>
          </a-row>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </page-header-wrapper>
</template>

<script>
import { getCartGoodsList, changeGoodsNumInCart, removeGoodsFromCart } from '@/api/shoppingCart'
import storage from "store";

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
  },
  {
    title: '操作',
    dataIndex: 'action',
    align: 'center',
    width: '70px',
    scopedSlots: { customRender: 'action' }
  }
]

const invalid_columns = [
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
  },
  {
    title: '操作',
    dataIndex: 'action',
    align: 'center',
    width: '70px',
    scopedSlots: { customRender: 'action' }
  }
]

const EditableCell = {
  template: `
      <div class="editable-cell">
        <div v-if="editable" class="editable-cell-input-wrapper">
          <a-input-number :defaultValue='record.num'
                          :min='1' :max='record.stockNum'
                          @change="handleChange" @pressEnter="check"/>
          <a-icon type="check" class="editable-cell-icon-check" @click="check" />
        </div>
        <div v-else class="editable-cell-text-wrapper">
          {{ value || ' ' }}
          <a-icon type="edit" class="editable-cell-icon" @click="edit" />
        </div>
      </div>
    `,
  props: {
    record: Object
  },
  data() {
    return {
      value: this.record.num,
      editable: false,
    };
  },
  methods: {
    handleChange(value) {
      this.value = value;
    },
    check() {
      this.editable = false;
      this.$emit('change', this.value);
    },
    edit() {
      this.editable = true;
    },
  },
};

export default {
  name: 'GoodManage',
  components: {
    EditableCell,
  },
  data() {
    return {
      columns,
      invalid_columns,
      EditableCell,

      data: [],
      pagination: {
        pageSize: 10,
        current: 1
      },
      loading: false,
      selectedRows: [],
      total_money: 0,
      rowSelection: {
        onChange: (selectedRowKeys, selectedRows) => {
          // console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows)
          this.selectedRows = selectedRows

          this.total_money = 0
          selectedRows.forEach(item => {
            this.total_money += item.num * item.price
          })
        },
        onSelect: (record, selected, selectedRows) => {
          // console.log(record, selected, selectedRows)
          this.selectedRows = selectedRows

          this.total_money = 0
          selectedRows.forEach(item => {
            this.total_money += item.num * item.price
          })
        },
        onSelectAll: (selected, selectedRows, changeRows) => {
          // console.log(selected, selectedRows, changeRows)
          this.selectedRows = selectedRows

          this.total_money = 0
          selectedRows.forEach(item => {
            this.total_money += item.num * item.price
          })
        }
      }
    }
  },

  mounted() {
    this.pagination = {
      pageSize: 10,
      current: 1
    }
    this.fetch()
  },

  methods: {
    fetch(params = {}) {
      this.loading = true
      getCartGoodsList({
        'pageSize': this.pagination.pageSize,
        'pageNo': this.pagination.current,
        ...params
      }).then((res) => {

        console.log(res)

        const pagination = { ...this.pagination }
        // Read total count from server
        // pagination.total = data.totalCount;
        pagination.total = res.data.data.totalNumOfItem
        this.pagination = pagination

        this.loading = false

        this.data = res.data.data.cart

      })
    },

    handleTableChange(pagination, filters, sorter) {
      const pager = { ...this.pagination }
      pager.current = pagination.current
      this.pagination = pager
      this.fetch({
        'pageSize': pagination.pageSize,
        'pageNo': pagination.current
      })
      this.total_money = 0
    },

    OnNumChange(key, dataIndex, value){
      changeGoodsNumInCart({
        'categoryId': key,
        'num': value
      }).then((res) => {
        this.$notification.success({
          message: '修改成功',
          description: res.data.detail
        })

        const dataSource = [...this.data];
        const target = dataSource.find(item => item.categoryId === key);
        if (target) {
          target[dataIndex] = value;
          this.data = dataSource;
        }
        if (this.selectedRows.findIndex((row) => row.categoryId === key) !== -1) {
          this.total_money = 0
          this.selectedRows.forEach(item => {
            this.total_money += item.num * item.price
          })
        }
      }).catch(err => {
        const res = err.response
        if (res.status === 409) {
          console.log("409")

          const dataSource = [...this.data];
          const target = dataSource.find(item => item.categoryId === key);
          if (target) {
            target['stockNum'] = res.data.num;
            this.data = dataSource;
          }
        } else if (res.status === 404) {
          console.log("404")

          const dataSource = [...this.data];
          const target = dataSource.find(item => item.categoryId === key);
          if (target) {
            target['stockNum'] = res.data.num;
            target['num'] = res.data.num;
            this.data = dataSource;
          }
        }
      }).finally()
    },

    removeFromCart(record) {
      let me = this
      this.$confirm({
        title: '确定从购物车中移除该商品?',
        onOk() {
          removeGoodsFromCart({
            'categoryList': [record.categoryId]
          }).then((res) => {
            me.$notification.success({
              message: '移除商品成功',
              description: res.data.detail
            })
            me.fetch()
          })
        },
        onCancel() {
        }
      })
    },

    removeManyFromCart() {
      let goods_list = []
      this.selectedRows.forEach(item => {
        goods_list.push(item.categoryId)
      })

      let me = this
      this.$confirm({
        title: '确定从购物车中批量移除这些商品?',
        onOk() {
          removeGoodsFromCart({
            'categoryList': goods_list
          }).then((res) => {
            me.$notification.success({
              message: '移除商品成功',
              description: res.data.detail
            })
            me.fetch()
          })
        },
        onCancel() {
        }
      })
    },

    buy() {
      this.$router.push({
        name: 'createOrder',
        params: {
          goods: this.selectedRows,
          isFromCart: true
        }
      })
    }
  }
}
</script>

<style>
.money {
  font-family: "Helvetica Neue", sans-serif;
  font-weight: 500;
  font-size: 20px;
  color: red;
  line-height: 14px;
}

.editable-cell {
  position: relative;
}

.editable-cell-input-wrapper,
.editable-cell-text-wrapper {
  padding-right: 24px;
}

.editable-cell-text-wrapper {
  padding: 5px 24px 5px 5px;
}

.editable-cell-icon,
.editable-cell-icon-check {
  position: absolute;
  right: 0;
  width: 20px;
  cursor: pointer;
}

.editable-cell-icon {
  line-height: 18px;
  display: none;
}

.editable-cell-icon-check {
  line-height: 28px;
}

.editable-cell:hover .editable-cell-icon {
  display: inline-block;
}

.editable-cell-icon:hover,
.editable-cell-icon-check:hover {
  color: #108ee9;
}

.editable-add-btn {
  margin-bottom: 8px;
}
</style>