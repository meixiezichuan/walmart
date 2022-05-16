import Mock from 'mockjs2'
import { builder, getQueryParameters } from '../util'

const totalCount = 56

const cart_goods = (options) => {
  const parameters = getQueryParameters(options)

  const goods = []
  const pageNo = parseInt(parameters.pageNo)
  const pageSize = parseInt(parameters.pageSize)
  const totalPage = Math.ceil(totalCount / pageSize)
  const key = (pageNo - 1) * pageSize
  const next = (pageNo >= totalPage ? (totalCount % pageSize) : pageSize) + 1

  for (let i = 1; i < next; i++) {
    goods.push({
      num: Mock.mock('@integer(0, 3)'),
      goods: {
        id: Mock.mock('@guid()'),
        name: Mock.mock('@cword(3, 5)'),
        description: Mock.mock('@cparagraph(1, 3)'),
        image: 'https://gw.alipayobjects.com/zos/rmsportal/dURIMkkrRFpPgTuzkwnB.png',
        num: Mock.mock('@integer(5, 10)'),
        user_id: Mock.mock('@guid()'),
        user_name: Mock.mock('@cname()'),
        category: Mock.mock('@cword(2)'),
        price: Mock.mock('@integer(1, 100)'),
      }
    })
  }

  return builder({
    totalNumOfGoods: totalCount,
    totalPage: totalPage,
    cart: goods
  })
}

Mock.mock(/\/cart/, 'get', cart_goods)