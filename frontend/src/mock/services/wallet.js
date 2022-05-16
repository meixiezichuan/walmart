import Mock from 'mockjs2'
import { builder, getQueryParameters } from '../util'


const wallet = (options) => {
  //const parameters = getQueryParameters(options)

  return builder({
    'balance': Mock.mock('@float(1,100)')
  })
}

Mock.mock(/\/wallet/, 'get', wallet)