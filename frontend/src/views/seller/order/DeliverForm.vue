<template>
  <a-select style="width: 200px" @change='handleChange'>
    <a-select-option v-for='company in companies' :key='company.id'>
      {{ company.name }}
    </a-select-option>
  </a-select>
</template>

<script>
import { getCompanyList } from '@/api/logisticsCompany'
import {changeOrderOfSeller} from "@/api/order";

export default {
  name: 'DeliverForm',
  props: {
    record: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      companies: [],
      company: ''
    }
  },
  mounted() {
    this.fetchLogisticsCompany()
  },
  methods: {
    fetchLogisticsCompany() {
      getCompanyList()
        .then(res => {
          this.companies = res.data.data
        })
    },
    handleChange(value){
      this.company = value
    },
    onOk() {
      return new Promise(resolve => {
        changeOrderOfSeller(this.record.id,
          {status: 4, logisticsCompanyId: this.company})
        .then(res => {
          this.$notification.success({
            message: '发货成功',
            description: res.data.detail
          })
          resolve(true)
        })
      })
    },
  }
}
</script>

<style scoped>

</style>