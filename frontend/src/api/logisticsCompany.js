import request  from '@/utils/request'

const api = {
  companyList: '/logistics_company'
}

export default api

export function getCompanyList (parameter) {
  return request({
    url: api.companyList,
    method: 'get'
  })
}