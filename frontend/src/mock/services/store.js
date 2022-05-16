import Mock from 'mockjs2'
import { builder, getQueryParameters} from "@/mock/util";

const stores = (options) => {
  const stores = []
  let pre = ""
  for(let i=0;i<3;i++){
    if(i%3===0){
      pre="商铺一"
    }else if(i%3===1){
      pre="商铺二"
    }else{
      pre="商铺三"
    }
    stores.push({
      id: Mock.mock(i),
      name: pre,
      description: Mock.mock('@cparagraph(1,3)'),
      userId: Mock.mock('@guid()'),
      createTime: Mock.mock('@datetime'),
      grade: Mock.mock('@integer(1,5)'),
      status: "Open"
    })
  }
  return builder({
    stores: stores
  })
}

Mock.mock(/\/store/, 'get', stores)