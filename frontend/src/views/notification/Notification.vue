<template>
  <page-header-wrapper>
    <a-card
      style='margin-top: 24px'
      :bordered='false'>
      <a-tabs default-active-key='1'>
        <a-tab-pane key='1' tab='未读消息'>
          <a-table
            bordered
            :columns='columns'
            :row-key='record => record.id'
            :data-source='data.filter(item => item.isRead === 0)'
            :loading='loading'
          >
            <span slot='action' slot-scope='text, record'>
              <template>
                <a @click='read(record)'>已读</a>
              </template>
            </span>

          </a-table>
        </a-tab-pane>
        <a-tab-pane key='2' tab='已读消息' force-render>
          <a-table
            bordered
            :columns='columns'
            :row-key='record => record.id'
            :data-source='data.filter(item => item.isRead === 1)'
            :loading='loading'
          >
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </page-header-wrapper>
</template>

<script>

import {getNotificationList, modifyNotification} from "@/api/notification";

const columns = [
  {
    title: '发送方',
    align: 'center',
    dataIndex: 'sender',
  },
  {
    title: '内容',
    align: 'center',
    dataIndex: 'content',
  },
  {
    title: '时间',
    align: 'center',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    dataIndex: 'action',
    align: 'center',
    width: '70px',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: "Notification",
  data(){
    return {
      loading: false,
      data: [],
      columns
    }
  },
  mounted() {
    this.fetch()
  },
  methods: {
    fetch() {
      this.loading = true
      getNotificationList().then((res) => {
        this.data = res.data.data
        this.loading = false
      })
    },
    read(record){
      modifyNotification(record.id, {isRead: 1})
      .then(res => {
        this.fetch()
      })
    }
  }
}
</script>

<style scoped>

</style>