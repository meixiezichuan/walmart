import Mock from 'mockjs2'
import { builder, getQueryParameters } from '../util'

const totalCount = 56

const orders = (options) => {
  const parameters = getQueryParameters(options)

  const orders = []
  const pageNo = parseInt(parameters.pageNo)
  const pageSize = parseInt(parameters.pageSize)
  const totalPage = Math.ceil(totalCount / pageSize)
  const key = (pageNo - 1) * pageSize
  const next = (pageNo >= totalPage ? (totalCount % pageSize) : pageSize) + 1

  for (let i = 1; i < next; i++) {
    const tmpKey = key + i
    orders.push({
      key: tmpKey,
      image: 'https://gw.alipayobjects.com/zos/rmsportal/dURIMkkrRFpPgTuzkwnB.png',
      id: Mock.mock('@guid()'),
      name: Mock.mock('@cword(3, 5)'),
      // userId: Mock.mock('@guid()'),
      // userName: Mock.mock('@cname()'),
      // description: Mock.mock('@cparagraph(1, 3)'),
      consignInfo: Mock.mock('@cparagraph(1, 3)'),
      num: Mock.mock('@integer(1, 100)'),
      price: Mock.mock('@integer(1, 100)'),
      status:Mock.mock('@integer(1, 5)'),
    })
  }

  return builder({
    totalNum: totalCount,
    totalPage: totalPage,
    orders: orders
  })
}

Mock.mock(/\/order\/subOrder\/list/, 'get', orders)