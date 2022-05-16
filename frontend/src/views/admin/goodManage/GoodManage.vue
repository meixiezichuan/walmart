<template>
  <page-header-wrapper>
    <a-card
      style='margin-top: 24px'
      :bordered='false'
      title='商品列表'>

      <a-collapse v-model="activeKey">
        <a-collapse-panel key="1" header="This is panel header 1">
          <p>{{ text }}</p>
        </a-collapse-panel>
        <a-collapse-panel key="2" header="This is panel header 2" :disabled="false">
          <p>{{ text }}</p>
        </a-collapse-panel>
        <a-collapse-panel key="3" header="This is panel header 3" disabled>
          <p>{{ text }}</p>
        </a-collapse-panel>
      </a-collapse>

      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="商品名称的关键字">
                <a-input v-model="queryParam.name" placeholder=""/>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <span class="table-page-search-submitButtons">
                <a-button type="primary" @click="fetch()">查询</a-button>
                <a-button style="margin-left: 8px" @click="() => this.queryParam = {}">重置</a-button>
              </span>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <div class='operate'>
        <a-button type='dashed' style='width: 100%' icon='plus' @click='add'>添加</a-button>
      </div>

      <a-table
        :columns='columns'
        :row-key='record => record.id'
        :data-source='data'
        :pagination='pagination'
        :loading='loading'
        @change='handleTableChange'
      >
        <span slot="pic" slot-scope="text, record">
          <img style="width:50px;height:50px" :src=record.image alt="暂无图片">
        </span>
        <span slot='action' slot-scope='text, record'>
          <template>
            <a @click='edit(record)'>编辑</a>
            <a-divider v-if='record.role !== 1' type='vertical' />
            <a v-if='record.role !== 1' @click='handleDelete(record)'>删除</a>
          </template>
        </span>
      </a-table>
    </a-card>
  </page-header-wrapper>
</template>

<script>
import moment from 'moment'
import { STable } from '@/components'
import {getGoodList} from '@/api/manage'
import EditForm from '@/views/admin/goodManage/EditForm'
import AddForm from '@/views/admin/goodManage/AddForm'
import {builder} from "@/mock/util";
import {mapActions} from "vuex";

export default {
  name: 'GoodManage',
  components: {
    STable
  },
  data() {
    return {
      // 表头
      columns: [
        {
          title: '图片',
          dataIndex: 'image',
          scopedSlots: { customRender: 'pic' }
        },
        {
          title: 'id',
          dataIndex: 'id',
          sorter: true,
          align: 'center'
        },
        {
          title: '名称',
          dataIndex: 'name',
          align: 'center'
        },
        {
          title: '介绍',
          dataIndex: 'description',
          align: 'center'
        },
        {
          title: '商户id',
          dataIndex: 'userId',
          align: 'center'
        },
        {
          title: '商户名',
          dataIndex: 'userName',
          align: 'center'
        },
        {
          title: '删除',
          dataIndex: 'deprecated',
          customRender: (text) => text ? '删除' : '未删除',
          align: 'center'
        },
        {
          title: '数量',
          dataIndex: 'num',
          // customRender: (text) => text === 1 ? '买家' : '商家'
          align: 'center'
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          width: '150px',
          scopedSlots: { customRender: 'action' }
        }
      ],
      //加载数据方法 必须为 Promise 对象
      loadData: parameter => {
        console.log('loadData.parameter', parameter)
        const param = { 'page': parameter.pageNo, 'pageNum': parameter.pageSize }
        console.log('param', param)
        return getGoodList(param)
          .then(res => {
            console.log(res)
            let result = builder({
              pageSize: parameter.pageSize,
              pageNo: parameter.pageNo,
              totalCount: 13,
              totalPage: 2,
              data: res.data.data
            })
            console.log(result)
            return result.result
          })
      },
      data: [],
      pagination: {
        pageSize: 10,
        current: 1
      },
      loading: false,

      queryParam: {},
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
    ...mapActions(['deleteGood']),

    handleTableChange (pagination, filters, sorter) {
      console.log(pagination)
      const pager = { ...this.pagination }
      pager.current = pagination.current
      this.pagination = pager
      this.fetch({
        'pageNum': pagination.pageSize,
        'page': pagination.current,
        ...this.queryParam
      })
    },
    fetch (params = {}) {
      this.loading = true
      getGoodList({
        'pageNum': this.pagination.pageSize,
        'page': this.pagination.current,
        ...this.queryParam,
        ...params
      }).then((res) => {
        console.log(res)
        const pagination = { ...this.pagination }
        // Read total count from server
        // pagination.total = data.totalCount;
        pagination.total = res.data.data.totalNumOfGoods
        this.loading = false
        this.data = res.data.data.goods
        this.pagination = pagination
      })
    },
    add() {
      let me = this
      this.$dialog(AddForm,
        // component props
        {
          record: {},
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
          title: '新增商品',
          width: 700,
          centered: true,
          maskClosable: false
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
          title: '更改商品信息',
          width: 700,
          centered: true,
          maskClosable: false
        })
      me.fetch()
    },
    handleDelete(record) {
      const { deleteGood } = this

      console.log('record', record)
      let deleteParam = {'id': record.id}
      console.log(deleteParam)
      let me = this
      this.$confirm({
        title: '确定删除该商品?',
        content: '点击ok按钮，该商品将在1秒后被删除。',
        onOk() {
          deleteGood(deleteParam)
            .then((res) => {
              me.$notification.success({
                message: '删除成功',
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
    }
  }
}
</script>