<template>
  <div>
    <a-card
      style='margin-top: 24px'
      :bordered='false'
      title='订单列表'>

      <div class="table-page-search-wrapper">
        <a-select v-model="queryParam.status" style="width: 120px" @change="fetch">
          <a-select-option :value=0 selected>
            全部
          </a-select-option>
          <a-select-option :value=2>
            待付款
          </a-select-option>
          <a-select-option :value=3>
            待发货
          </a-select-option>
          <a-select-option :value=4>
            待收货
          </a-select-option>
          <a-select-option :value=1>
            已完成
          </a-select-option>
          <a-select-option :value=5>
            已取消
          </a-select-option>
        </a-select>
        <!--            <a-col>-->
        <!--              <span class="table-page-search-submitButtons">-->
        <!--                <a-button type="primary" @click="fetch()">查询</a-button>-->
        <!--                <a-button style="margin-left: 8px" @click="() => this.queryParam = {}">重置</a-button>-->
        <!--              </span>-->
        <!--            </a-col>-->
      </div>

      <a-table
        :columns='columns'
        :row-key='record => record.id'
        :data-source='data'
        :pagination='pagination'
        :loading='loading'
        @change='handleTableChange'
      >
        <span slot='action' slot-scope='text, record'>
            <template>
              <a v-if='record.status === 2' @click='pay(record)'>付款</a>
              <a-divider v-if='record.status === 2' type='vertical'/>
              <a v-if='record.status === 2' @click='edit(record)'>修改</a>
              <a-divider v-if='record.status === 2' type='vertical'/>
              <a v-if='record.status === 2' @click='handleCancel(record)'>取消</a>

              <a-divider v-if='record.status === 4' type='vertical'/>
              <a v-if='record.status === 4' @click='confirm(record)'>确认收货</a>

            </template>
        </span>
        <div slot="expandedRowRender" slot-scope="record" style="margin: 0">
          <a-table :data-source="record.orderItems" :columns="innerColumns" :pagination=false
                   @change='handleTableChange' size="small">
              <span slot="pic" slot-scope="text, record">
                <img style="width:50px;height:50px" :src=record.goodsImage alt="暂无图片">
              </span>
          </a-table>
        </div>
      </a-table>
    </a-card>
  </div>
</template>

<script>
import {STable} from '@/components'
import {changeOrderOfBuyer, getOrdersList, paySubOrder} from '@/api/order'
import EditForm from '@/views/buyer/order/orderManage/EditForm'
import {mapActions} from "vuex";

export default {
  name: 'OrderManage',
  components: {
    STable
  },
  data() {
    return {
      // 表头
      columns: [
        {
          title: 'id',
          dataIndex: 'id',
          align: 'center'
        },
        {
          title: '店铺',
          dataIndex: 'storeName',
          align: 'center'
        },
        {
          title: '总价',
          dataIndex: 'totalPrice',
          align: 'center'
        },
        {
          title: '地址',
          dataIndex: 'consignInfo',
          align: 'center'
        },
        {
          title: '状态',
          dataIndex: 'status',
          customRender: (text) => {
            switch (text) {
              case 2:
                return "待付款";
              case 3:
                return "待发货";
              case 4:
                return "待收货";
              case 1:
                return "已完成";
              case 5:
                return "已取消";
            }
            return "Unknown";
          },
          align: 'center'
        },
        {
          title: '创建时间',
          dataIndex: 'createTime',
          align: 'center',
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',

          width: '150px',
          scopedSlots: {customRender: 'action'}
        }
      ],
      innerColumns: [
        {
          title: '图片',
          dataIndex: 'goodsImage',
          scopedSlots: {customRender: 'pic'}
        },
        {
          title: 'id',
          dataIndex: 'id',
          sorter: true,
          align: 'center'
        },
        {
          title: '名称',
          dataIndex: 'goodsName',
          align: 'center'
        },
        {
          title: '类别',
          dataIndex: 'goodsCategoryName',
          align: 'center'
        },
        {
          title: '描述',
          dataIndex: 'goodsDescription',
          align: 'center'
        },
        {
          title: '数量',
          dataIndex: 'num',
          align: 'center'
        },
        {
          title: '价格',
          dataIndex: 'price',
          align: 'center'
        },
      ],
      data: [],
      pagination: {
        pageSize: 10,
        current: 1
      },
      loading: false,

      queryParam: {'status': 0},
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
    ...mapActions(['cancelOrder']),

    handleTableChange(pagination, filters, sorter) {
      console.log(pagination)
      const pager = {...this.pagination}
      pager.current = pagination.current
      this.pagination = pager
      this.fetch({
        'pageNum': pagination.pageSize,
        'page': pagination.current,
        ...this.queryParam
      })
    },
    fetch(params = {}) {
      this.loading = true
      getOrdersList({
        'pageSize': this.pagination.pageSize,
        'pageNo': this.pagination.current,
        ...this.queryParam,
        ...params
      }).then((res) => {
        console.log(this.queryParam)
        console.log(res)
        const pagination = {...this.pagination}
        // Read total count from server
        // pagination.total = data.totalCount;
        pagination.total = res.data.data.total_num
        this.loading = false
        this.data = res.data.data.suborders
        this.pagination = pagination
      })
    },
    pay(record) {
      const that = this
      console.log(record.id)
      paySubOrder(record.id).then(res => {
        this.$notification.success({
          message: '支付成功',
          description: res.data.detail
        })
        that.fetch()

      })
    },
    edit(record) {
      let me = this
      console.log('record', record)
      this.$dialog(EditForm,
        // component props
        {
          record,
          on: {
            ok() {
              console.log('ok 回调')
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
          title: '更改订单信息',
          width: 700,
          centered: true,
          maskClosable: false
        })
    },
    handleCancel(record) {
      const {cancelOrder} = this

      console.log('record', record)
      let deleteParam = {'id': record.id, 'status': 5}
      console.log(deleteParam)
      let me = this
      this.$confirm({
        title: '确定取消该订单?',
        content: '点击ok按钮，该订单将会被取消。',
        onOk() {
          cancelOrder(deleteParam)
            .then((res) => {
              me.$notification.success({
                message: '取消成功',
                description: res.detail
              })
              me.pagination.current = 1
              me.fetch()
            })
            .catch()
            .finally(() => {
            })
        },
        onCancel() {
        }
      })
    },
    confirm(record){
      changeOrderOfBuyer(record.id, {status: 1})
        .then(res => {
          this.$notification.success({
            message: '确认收货成功',
            description: res.data.detail
          })
          this.fetch()
        })
    }
  }
}
</script>