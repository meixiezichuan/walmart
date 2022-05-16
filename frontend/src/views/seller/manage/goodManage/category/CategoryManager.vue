<template>
  <page-header-wrapper>
    <a-card
      style='margin-top: 24px'
      :bordered='false'
      title='商品分类列表'>

      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="商品分类名称的关键字">
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

      <div class='operate' style="margin-bottom: 20px">
        <a-button type='dashed' style='width: 100%' icon='plus' @click='add'>添加</a-button>
      </div>

      <a-table
        :columns='columns'
        :row-key='record => record.id'
        :data-source='data'
        :loading='loading'
        @change='handleTableChange'
      >
        <span slot="price" slot-scope="text, record">
          {{record.price ? record.price.toFixed(2): '0.00'}}
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
import { STable } from '@/components'
import {deleteGoodsCategory, getGoodList, getGoodsCategory} from '@/api/manage'
import {builder} from "@/mock/util";
import {mapActions} from "vuex";
import EditCategoryForm from "@/views/seller/manage/goodManage/category/EditCategoryForm";
import AddCategoryForm from "@/views/seller/manage/goodManage/category/AddCategoryForm";

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
          title: '商品类别名称',
          dataIndex: 'name',
          sorter: true,
          align: 'center'
        },
        {
          title: '价格',
          dataIndex: 'price',
          scopedSlots: { customRender: 'price' },
          align: 'center'
        },
        {
          title: '数量',
          dataIndex: 'num',
          customRender: (text) => text? text : 0,
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
        const param = { 'goodsId': this.$route.params.goodsId }
        console.log('param', param)
        return getGoodsCategory(param)
          .then(res => {
            console.log(res)
            let result = builder({
              data: res.data.data
            })
            console.log(result)
            return result.result
          })
      },
      data: [],
      loading: false,

      queryParam: { 'goodsId': this.$route.params.goodsId },
    }
  },

  mounted() {
    this.fetch()
  },

  methods: {
    ...mapActions(['deleteGood']),

    handleTableChange (filters, sorter) {
      this.fetch({
        ...this.queryParam
      })
    },
    fetch (params = {}) {
      this.loading = true
      console.log(this.queryParam)
      console.log(params)
      getGoodsCategory({
        ...this.queryParam,
        ...params
      }).then((res) => {
        console.log(res)
        this.loading = false
        this.data = res.data.data.categories
      })
    },
    add() {
      let me = this
      this.$dialog(AddCategoryForm,
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
          title: '新增商品类别',
          width: 700,
          centered: true,
          maskClosable: false
        })
    },

    edit(record) {
      let me = this
      console.log('record', record)
      this.$dialog(EditCategoryForm,
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
          title: '更改商品类别信息',
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
        title: '确定删除该商品类别?',
        content: '点击ok按钮，该商品类别将在1秒后被删除。',
        onOk() {
          deleteGoodsCategory(deleteParam)
            .then((res) => {
              me.$notification.success({
                message: '删除商品类别成功',
                description: res.data.detail

              })
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