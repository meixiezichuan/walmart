import Mock from 'mockjs2'
import {builder, getQueryParameters} from "@/mock/util";

const totalCount = 56
const goods = (options) =>{
  const parameters = getQueryParameters(options)

  const goods = []
  const pageNo = parseInt(parameters.page)
  const pageSize = parseInt(parameters.pageNum)
  const totalPage = Math.ceil(totalCount / pageSize)
  const key = (pageNo - 1) * pageSize
  const next = (pageNo >= totalPage ? (totalCount % pageSize) : pageSize) + 1
  let pre=""
  for (let i=1;i<next;i++){
    let tmpKey = key + i
    console.log('storeId', parameters.storeId)
    if(parameters.storeId==='0')
      pre="商铺一"
    else if(parameters.storeId==='1')
      pre="商铺二"
    else
      pre="商铺三"
    goods.push({
      key:tmpKey,
      image: 'https://gw.alipayobjects.com/zos/rmsportal/dURIMkkrRFpPgTuzkwnB.png',
      id: Mock.mock('@guid()'),
      name: pre+'_'+Mock.mock('@cword(3,5)'),
      userId: Mock.mock('@guid()'),
      userName: Mock.mock('@cword(3,5'),
      storeId: Mock.mock(parameters.storeId),
      storeName: pre
    })
  }
  return builder({
    totalNumOfGoods: totalCount,
    totalPage: totalPage,
    goods: goods
  })
}

Mock.mock(/\/goods/, 'get',goods)