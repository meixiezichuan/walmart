<template>
  <page-header-wrapper>
    <a-card
      style='margin-top: 24px'
      :bordered='false'
      title='用户列表'>

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
        <span slot='action' slot-scope='text, record'>
          <template>
            <a @click='edit(record)'>编辑</a>
            <a-divider v-if='record.role !== 1' type='vertical' />
            <a v-if='record.role !== 1' @click='handleDelete(record)'>删除</a>
          </template>
        </span>
      </a-table>

      <!--      <s-table-->
      <!--        ref='stable'-->
      <!--        size='default'-->
      <!--        rowKey='key'-->
      <!--        :columns='columns'-->
      <!--        :data='loadData'-->
      <!--      >-->
      <!--        <a-avatar slot='avatar' slot-scope='text' size='large' shape='square' :src='text' />-->
      <!--        <span slot='action' slot-scope='text, record'>-->
      <!--        <template>-->
      <!--          <a @click='edit(record)'>编辑</a>-->
      <!--          <a-divider type='vertical' />-->
      <!--          <a @click='handleDelete(record)'>删除</a>-->
      <!--        </template>-->
      <!--      </span>-->
      <!--      </s-table>-->
    </a-card>
  </page-header-wrapper>
</template>

<script>
import { STable } from '@/components'
import { getUserList } from '@/api/manage'
import EditForm from '@/views/admin/userManage/EditForm'
import AddForm from '@/views/admin/userManage/AddForm'
import { builder } from '@/mock/util'
import { mapActions } from 'vuex'

export default {
  name: 'UserManage',

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
          sorter: true,
          align: 'center'
        },
        {
          title: '用户名',
          dataIndex: 'name',
          align: 'center'
        },
        {
          title: '角色',
          dataIndex: 'role',
          customRender: (text) => (text === 1 ? '管理员' : (text === 2 ? '商家' : '买家')),
          align: 'center'
        },
        {
          title: '邮箱',
          dataIndex: 'email',
          align: 'center'
        },
        {
          title: '电话',
          dataIndex: 'phone_num',
          align: 'center'
        },
        {
          title: '地址',
          dataIndex: 'address',
          align: 'center'
        },
        {
          title: '操作',
          dataIndex: 'action',
          width: '150px',
          align: 'center',
          scopedSlots: { customRender: 'action' }
        }
      ],
      //加载数据方法 必须为 Promise 对象
      loadData: parameter => {
        console.log(parameter)
        const param = { 'page_num': parameter.pageNo, 'page_size': parameter.pageSize }
        console.log('param', param)
        return getUserList(param)
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
    ...mapActions(['deleteUser']),

    handleTableChange(pagination, filters, sorter) {
      console.log(pagination)
      const pager = { ...this.pagination }
      pager.current = pagination.current
      this.pagination = pager
      this.fetch({
        'page_size': pagination.pageSize,
        'page_num': pagination.current
      })
    },
    fetch(params = {}) {
      this.loading = true
      getUserList({
        'page_size': this.pagination.pageSize,
        'page_num': this.pagination.current,
        ...params
      }).then((res) => {
        console.log(res)
        const pagination = { ...this.pagination }
        // Read total count from server
        // pagination.total = data.totalCount;
        pagination.total = res.data.data.total_num
        this.loading = false
        this.data = res.data.data.user_list
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
          title: '新增用户',
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
          title: '更改用户信息',
          width: 700,
          centered: true,
          maskClosable: false
        })
      me.fetch()
    },
    handleDelete(record) {
      const { deleteUser } = this

      console.log('record', record)
      let deleteParam = {'id': record.id}
      console.log(deleteParam)
      let me = this
      this.$confirm({
        title: '确定删除该用户?',
        content: '点击ok按钮，该用户将在1秒后被删除。',
        onOk() {
          deleteUser(deleteParam)
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
