<!--
  * 销售订单表
  *
  * @Author:    yxz
  * @Date:      2025-10-23 14:10:32
  * @Copyright  (c)2024 yxz
-->
<template>
  <a-modal
      :title="form.salesOrderId ? '编辑' : '添加'"
      :width="500"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="单据编号-销售订单"  name="billNo">
          <a-input style="width: 100%" v-model:value="form.billNo" placeholder="单据编号-销售订单" />
        </a-form-item>
        <a-form-item label="单据日期"  name="orderDate">
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.orderDate" style="width: 100%" placeholder="单据日期"/>
        </a-form-item>
        <a-form-item label="客户编码"  name="customerCode">
          <a-input style="width: 100%" v-model:value="form.customerCode" placeholder="客户编码" />
        </a-form-item>
        <a-form-item label="销售员编码"  name="salespersonCode">
          <a-input style="width: 100%" v-model:value="form.salespersonCode" placeholder="销售员编码" />
        </a-form-item>
        <a-form-item label="单据类型（0配件 1整车"  name="orderType">
          <a-input-number style="width: 100%" v-model:value="form.orderType" placeholder="单据类型（0配件 1整车" />
        </a-form-item>
        <a-form-item label="价税合计"  name="amount">
          <a-input-number style="width: 100%" v-model:value="form.amount" placeholder="价税合计" />
        </a-form-item>
        <a-form-item label="汇率"  name="exchangeRate">
          <a-input-number style="width: 100%" v-model:value="form.exchangeRate" placeholder="汇率" />
        </a-form-item>
        <a-form-item label="价税合计（本位币）"  name="fallAmount">
          <a-input-number style="width: 100%" v-model:value="form.fallAmount" placeholder="价税合计（本位币）" />
        </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
        <a-button type="primary" @click="onSubmit">保存</a-button>
      </a-space>
    </template>
  </a-modal>
</template>
<script setup>
  import { reactive, ref, nextTick } from 'vue';
  import _ from 'lodash';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { salesOrderApi } from '/@/api/vigorous/sales-order-api';
  import { smartSentry } from '/@/lib/smart-sentry';

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
      billNo: undefined, //单据编号-销售订单
      orderDate: undefined, //单据日期
      customerCode: undefined, //客户编码
      salespersonCode: undefined, //销售员编码
      orderType: undefined, //单据类型（0配件 1整车
      amount: undefined, //价税合计
      exchangeRate: undefined, //汇率
      fallAmount: undefined, //价税合计（本位币）
  };

  let form = reactive({ ...formDefault });

  const rules = {
      billNo: [{ required: true, message: '单据编号-销售订单 必填' }],
      orderDate: [{ required: true, message: '单据日期 必填' }],
      customerCode: [{ required: true, message: '客户编码 必填' }],
      orderType: [{ required: true, message: '单据类型（0配件 1整车 必填' }],
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
      if (form.salesOrderId) {
        await salesOrderApi.update(form);
      } else {
        await salesOrderApi.add(form);
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
