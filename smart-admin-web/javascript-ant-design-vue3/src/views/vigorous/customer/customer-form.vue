<!--
  * 顾客
  *
  * @Author:    yxz
  * @Date:      2024-12-12 14:51:07
  * @Copyright  (c)2024 yxz
-->
<template>
  <a-drawer
      :title="form.customerId ? '编辑' : '添加'"
      :width="500"
      :open="visibleFlag"
      @close="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="客户编码"  name="customerCode">
          <a-input style="width: 100%" v-model:value="form.customerCode" placeholder="客户编码" />
        </a-form-item>
        <a-form-item label="客户名称"  name="customerName">
          <a-input style="width: 100%" v-model:value="form.customerName" placeholder="客户名称" />
        </a-form-item>
        <a-form-item label="简称"  name="shortName">
          <a-input style="width: 100%" v-model:value="form.shortName" placeholder="简称" />
        </a-form-item>
        <a-form-item label="国家"  name="country">
          <a-input style="width: 100%" v-model:value="form.country" placeholder="国家" />
        </a-form-item>
        <a-form-item label="币别"  name="currencyType">
          <DictSelect width="100%" v-model:value="form.currencyType" mode="" keyCode="CURRENCY_TYPE" placeholder="币别"/>
        </a-form-item>
        <a-form-item label="客户分组"  name="customerGroup">
          <SmartEnumSelect enum-name="CUSTOMER_GROUP_ENUM" v-model:value="form.customerGroup" width="100%"/>
        </a-form-item>
        <a-form-item label="转交情况"  name="transferStatus">
          <SmartEnumSelect enum-name="TRANSFER_STATUS_ENUM" v-model:value="form.transferStatus" width="100%"/>
        </a-form-item>
        <a-form-item label="是否报关"  name="isCustomsDeclaration">
          <SmartEnumSelect enum-name="SYSTEM_YES_NO" v-model:value="form.isCustomsDeclaration" width="100%"/>
        </a-form-item>
        <a-form-item label="首单日期"  name="firstOrderDate">
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.firstOrderDate" style="width: 100%" placeholder="首单日期"/>
        </a-form-item>
        <a-form-item label="客户类别"  name="customerCategory">
          <a-input style="width: 100%" v-model:value="form.customerCategory" placeholder="客户类别" />
        </a-form-item>
        <a-form-item label="业务员"  name="salespersonId">
          <SalespersonSelect width="100%" v-model:value="form.salespersonId" placeholder=""/>
        </a-form-item>
        <a-form-item label="创建日期"  name="createDate">
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.createDate" style="width: 100%" placeholder="金蝶-创建日期"/>
        </a-form-item>

    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
        <a-button type="primary" @click="onSubmit">保存</a-button>
      </a-space>
    </template>
  </a-drawer>
</template>
<script setup>
import {reactive, ref, nextTick, computed} from 'vue';
  import _ from 'lodash';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { customerApi } from '/@/api/vigorous/customer-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
  import SalespersonSelect from "/@/components/vigorous/salesperson-select/index.vue";
  import DictSelect from "/@/components/support/dict-select/index.vue";

  // ------------------------ 数据 ------------------------
  const salespersons = []
  const loading = ref(false)

  // ------------------------ 事件 ------------------------

  const emits = defineEmits(['reloadList']);

  // ------------------------ 显示与隐藏 ------------------------
  // 是否显示
  const visibleFlag = ref(false);

  function show(rowData) {
    Object.assign(form, formDefault);
    if (rowData && !_.isEmpty(rowData)) {
      Object.assign(form, rowData);
    }
    // 使用字典时把下面这注释修改成自己的字典字段 有多个字典字段就复制多份同理修改 不然打开表单时不显示字典初始值
    // if (form.status && form.status.length > 0) {
    //   form.status = form.status.map((e) => e.valueCode);
    // }
    visibleFlag.value = true;
    nextTick(() => {
      formRef.value.clearValidate();
    });
  }

  function onClose() {
    Object.assign(form, formDefault);
    visibleFlag.value = false;
  }

  // ------------------------ 表单 ------------------------

  // 组件ref
  const formRef = ref();

  const formDefault = {
      customerId: undefined,
      customerName: undefined, //客户名称
      shortName: undefined, //简称
      country: undefined, //国家
      customerGroup: undefined, //客户分组
      currencyType: undefined, //币别
      customerCategory: undefined, //客户类别
      salespersonId: undefined, //业务员
      customerCode: undefined, //客户编码
      transferStatus: undefined, //转交情况
      isCustomsDeclaration: undefined, //转交情况
      firstOrderDate: undefined,
      createDate: undefined,  // 金蝶-创建日期
  };

  let form = reactive({ ...formDefault });

  const rules = {
    customerCode: [{ required: true, message: '客户编码 必填' }],
    customerName: [{ required: true, message: '客户名称 必填' }],
    currencyType: [{ required: true, message: '币别 必填' }],
    transferStatus: [{ required: true, message: '转交情况 必填' }],
    isCustomsDeclaration: [{ required: true, message: '是否报关 必填' }],
    salespersonId: [{ required: true, message: '业务员 必填' }],
  };

  // 点击确定，验证表单
  async function onSubmit() {
    try {
      await formRef.value.validateFields();
      save();
    } catch (err) {
      message.error('参数验证错误，请仔细填写表单数据!');
    }
  }

  // 新建、编辑API
  async function save() {
    SmartLoading.show();
    try {
      if (form.customerId) {
        await customerApi.update(form);
      } else {
        await customerApi.add(form);
      }
      message.success('操作成功');
      emits('reloadList');
      onClose();
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  defineExpose({
    show,
  });
</script>
