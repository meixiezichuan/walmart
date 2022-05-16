<template>
  <page-header-wrapper>
    <div class="demo-dropdown-wrap">
      <a-select style="width: 120px" v-model="selectedStore" @change="handleStoreChange">
        <a-select-option v-for="store in stores" :key='store.id'>
          {{store.name}}
        </a-select-option>
      </a-select>
    </div>
    <a-card
      style='margin-top: 24px'
      :bordered='false'
      title='商品列表'>

      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="商品名称">
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
      <div class='gutter-example'>
        <a-row :gutter='24'>
          <a-col v-for='d in data' :key='d.id' class='gutter-row' :span='8'>
            <div class='gutter-box' >
              <a-card hoverable style="width: 300px; margin-bottom: 20px; margin-top: 20px">
                <img
                  slot="cover"
                  alt="example"
                  width="300px"
                  height="300px"
                  style="max-width: 100%; max-height: 100%"
                  :src='d.image ? d.image : imgData'
                />
                <template slot="actions" class="ant-card-actions">
                  <a-button style="padding-left: 3px; padding-right: 3px; background-color: #e3d142; color: white" @click='categoryManage(d)'>商品分类管理</a-button>
                  <a-button @click='edit(d)'>编辑</a-button>
<!--                  <a-divider v-if='record.role !== 2' type='vertical' />-->
                  <a-button @click='handleDelete(d)'>删除</a-button>
                </template>
                <a-card-meta :title='d.name + " " + (d.storeName? "[" + d.storeName+"]":"")' >
                  <a-avatar
                    slot="avatar"
                    icon="user"
                  />
                  <template #description>
                    <p class="card_description_style" style="height: 40px;white-space: pre-wrap;">{{d.description?d.description:"该商品未提供简介～"}}</p>
                  </template>
                </a-card-meta>
              </a-card>
            </div>
          </a-col>
        </a-row>
      </div>
      <div style='text-align: center; margin-top: 20px'>
        <a-pagination style='display: inline-block;' :default-current=pagination.current :page-size.sync=pagination.pageSize :total=pagination.total @change="onChange" />
      </div>
    </a-card>
  </page-header-wrapper>
</template>

<script>
import { STable } from '@/components'
import { getGoodList } from '@/api/manage'
import AddForm from '@/views/seller/manage/goodManage/AddForm'
import { builder } from '@/mock/util';
import { mapActions } from 'vuex';
import EditForm from '@/views/seller/manage/goodManage/EditForm';
import storage from 'store';
import {UserOutlined, DownOutlined} from "@ant-design/icons-vue";
import {getStores} from "@/api/store";
import TagSelectOption from "@/components/TagSelect/TagSelectOption";

export default {
  name: 'GoodManage',
  components: {
    TagSelectOption,
    STable,
    UserOutlined,
    DownOutlined,

  },
  data () {
    return {
      imgData: 'https://gitee.com/cuiyunna/walmart-image/raw/master/goods.png',
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
        // {
        //   title: '删除',
        //   dataIndex: 'deprecated',
        //   customRender: (text) => text ? '删除' : '未删除',
        //   align: 'center'
        // },
        // {
        //   title: '数量',
        //   dataIndex: 'num',
        //   // customRender: (text) => text === 1 ? '买家' : '商家'
        //   align: 'center'
        // },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          width: '150px',
          scopedSlots: { customRender: 'action' }
        }
      ],
      data: [],
      pagination: {
        pageSize: 12,
        current: 1
      },
      loading: false,
      selectedStore: "No Stores",
      stores: [],
      queryParam: {}
    }
  },

  mounted () {
    this.pagination = {
      pageSize: 12,
      current: 1
    }
    getStores({'userId': storage.get('user').id})
      .then((res) => {
        console.log("stores", res)
        this.stores = res.data.data.stores
        if(this.stores.length>0){
          this.selectedStore = this.stores[0].id;
        }
        this.fetch()
      })
  },

  methods: {
    ...mapActions(['deleteGood']),

    fetch(params = {}) {
      this.loading = true
      getGoodList({
        'pageNum': this.pagination.pageSize,
        'page': this.pagination.current,
        'userId': storage.get('user').id,
        'storeId': this.selectedStore,
        ...this.queryParam,
        ...params
      }).then((res) => {
        console.log("goods", res)
        const pagination = { ...this.pagination }

        pagination.total = res.data.data.totalNumOfGoods
        this.loading = false
        this.data = res.data.data.goods
        this.pagination = pagination
      })
    },

    handleStoreChange(value){
      this.fetch()
    },
    add() {
      this.$router.push({
        name: 'addGoods',
        params:{
          storeId: this.selectedStore
        }
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
    },
    handleDelete(record) {
      const { deleteGood } = this

      console.log('record', record)
      let deleteParam = {'id': record.id}
      console.log(deleteParam)
      let me = this
      this.$confirm({
        title: '确定删除该商品?',
        // content: '点击ok按钮，该商品将在1秒后被删除。',
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
    },
    onChange (current) {
      this.pagination.current = current;
      console.log(this.pagination.current)
      this.fetch({
        'pageNum': this.pagination.pageSize,
        'page': this.pagination.current
      })
    },
    categoryManage (record){
      console.log(record.id)
      this.$router.push({
        name: 'categoryManage',
        params:{
          goodsId: record.id
        }})
    }
  }
}
</script>
<style scoped>
.card_description_style
{
  overflow: hidden;
  -webkit-line-clamp: 2;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
}
.demo-dropdown-wrap :deep(.ant-dropdown-button) {
  margin-right: 8px;
  margin-bottom: 8px;
}
</style>
