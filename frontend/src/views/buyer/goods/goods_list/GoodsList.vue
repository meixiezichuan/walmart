<template>
  <page-header-wrapper>
    <div :style="{ background: '#fff', padding: '24px', minHeight: '380px'}">
      <a-input-search placeholder="input search text" enter-button @search="onSearch" style="padding-bottom: 20px"/>
      <div class='gutter-example'>
        <a-row :gutter='24'>
          <a-col v-for='item in items' :key='item.id' class='gutter-row' :span='8'>
            <div class='gutter-box' >
              <a-card hoverable style="width: 300px">
                <img
                  slot="cover"
                  alt="example"
                  width="300px"
                  height="300px"
                  style="max-width: 100%; max-height: 100%"
                  :src='item.image ? item.image : imgData'
                />
                <template slot="actions" class="ant-card-actions">
                  <a-button style="padding-left: 5px; padding-right: 5px; background-color: #e3d142; color: white" @click="goToCart(item)">加入购物车</a-button>
                  <a-button style="background-color: #e3ab42; color: white" @click="directShopping(item)">立即购买</a-button>
                </template>
                <a-card-meta :title='item.name + "\n" +  (item.storeName? "[" + item.storeName+"]":"")'>
                  <template #description>
                    <p class="card_description_style" style="height: 40px">{{item.description?item.description:"该商品未提供简介～"}}</p>
                  </template>
                </a-card-meta>
              </a-card>
            </div>
          </a-col>
        </a-row>
      </div>
      <div style='text-align: center; margin-top: 20px'>
        <a-pagination style='display: inline-block;' :default-current=parameter.page :page-size.sync=parameter.pageNum :total=pageTotal @change="onChange" />
      </div>
    </div>
  </page-header-wrapper>
</template>
<script>
import { getGoodsList } from '@/api/buyer'
import GoodsCart from "@/views/buyer/goods/goods_list/GoodsCart";

export default {
  name: 'Goods',
  data() {
    return {
      items: [],
      pageTotal: 0,
      parameter: {
        name: '',
        page: 1,
        pageNum: 12
      },
      imgData: 'https://gitee.com/cuiyunna/walmart-image/raw/master/goods.png'
    }
  },
  mounted() {
    this.$nextTick(function() {
      this.onChange(1)
    })
  },
  methods: {
    getGoodsList(parameter) {
      getGoodsList(parameter).then(response => {
        console.log(response)
        this.pageTotal = response.data.data.totalNumOfGoods
        const goodsList = response.data.data.goods
        this.items = goodsList
      })
    },
    onChange(current) {
      this.parameter.page = current;
      console.log(this.parameter.page)
      this.getGoodsList(this.parameter)
    },
    onSearch(value) {
      console.log(value);
      this.parameter.name = value
      this.getGoodsList(this.parameter)
    },
    goToCart(record) {
      console.log("record", record)
      this.$dialog(GoodsCart,
        {
          record,
          isCart: true,
          on: {
            ok () {
              console.log('ok 回调')
            },
            cancel () {
              console.log('cancel 回调')
            },
            close () {
              console.log('modal close 回调')
            }
          }
        },
        {
          title: '选择规格与数量',
          width: 500,
          centered: true,
          maskClosable: false
        }
      )
    },
    directShopping(record){
      let me = this
      console.log("record", record)
      this.$dialog(GoodsCart,
        {
          record,
          isCart: false,
          on: {
            ok () {
              console.log('ok 回调')
            },
            cancel () {
              console.log('cancel 回调')
            },
            close () {
              console.log('modal close 回调')
            }
          }
        })
    }
  },
}
</script>
<style scoped>
#components-layout-demo-fixed .logo {
  width: 120px;
  height: 31px;
  background: rgba(255, 255, 255, 0.2);
  margin: 16px 24px 16px 0;
  float: left;
}

.gutter-example>>>.ant-row>div {
  background: transparent;
  border: 0;
}

.gutter-box {
  /*background: #00a0e9;*/
  padding: 10px 0;
}

.img-goods {
  position: relative;
  width: 100%;
  height: 0;
  padding-bottom: 100%;
  overflow: hidden;
}

.img-goods img {
  position: absolute;
  width: 100%;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  margin: auto;
}
 .card_description_style
 {
   overflow: hidden;
   -webkit-line-clamp: 2;
   text-overflow: ellipsis;
   display: -webkit-box;
   -webkit-box-orient: vertical;
 }
</style>
