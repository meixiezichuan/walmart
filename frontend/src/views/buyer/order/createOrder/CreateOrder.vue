<template>
  <page-header-wrapper>
    <a-card :bordered="false">
      <a-steps class="steps" :current="currentTab">
        <a-step title="确认商品信息" />
        <a-step title="填写收货信息并下单" />
        <a-step title="付款" />
        <a-step title="完成" />
      </a-steps>
      <div class="content">
        <step1 v-if="currentTab === 0" @nextStep="nextStep"/>
        <step2 v-if="currentTab === 1" @nextStep="nextStep" @prevStep="prevStep"/>
        <step3 v-if="currentTab === 2" @nextStep="nextStep" @prevStep="prevStep" :order="order"/>
        <step4 v-if="currentTab === 3" @prevStep="prevStep" @finish="finish"/>
      </div>
    </a-card>
  </page-header-wrapper>
</template>

<script>
import Step1 from './Step1'
import Step2 from './Step2'
import Step3 from './Step3'
import Step4 from './Step4'

export default {
  name: 'StepForm',
  components: {
    Step1,
    Step2,
    Step3,
    Step4
  },
  data () {
    return {
      currentTab: 0,
      // form
      form: null,
      order: Object
    }
  },
  methods: {

    // handler
    nextStep (val) {
      if (this.currentTab < 3) {
        this.currentTab += 1
      }
      if(val){
        console.log(val)
        this.order = val
      }
    },
    prevStep () {
      if (this.currentTab > 0) {
        this.currentTab -= 1
      }
    },
    finish () {
      this.currentTab = 0
    }
  }
}
</script>

<style lang="less" scoped>
.steps {
  max-width: 750px;
  margin: 16px auto;
}
</style>
