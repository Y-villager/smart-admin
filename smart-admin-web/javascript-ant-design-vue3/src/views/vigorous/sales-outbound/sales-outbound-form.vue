<!--
  * 销售出库
  *
  * @Author:    yxz
  * @Date:      2024-12-12 14:48:19
  * @Copyright  (c)2024 yxz
-->
<template>
  <a-drawer
      :title="form.salesBoundId ? '编辑' : '添加'"
      :width="500"
      :open="visibleFlag"
      @close="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="主键"  name="salesBoundId">
          <a-input-number style="width: 100%" v-model:value="form.salesBoundId" placeholder="主键" />
        </a-form-item>
        <a-form-item label="单据编号"  name="billNo">
          <a-input style="width: 100%" v-model:value="form.billNo" placeholder="单据编号" />
        </a-form-item>
        <a-form-item label="出库日期"  name="salesBoundDate">
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.salesBoundDate" style="width: 100%" placeholder="出库日期"/>
        </a-form-item>
        <a-form-item label="客户编码"  name="customerCode">
          <a-input style="width: 100%" v-model:value="form.customerCode" placeholder="客户编码" />
        </a-form-item>
        <a-form-item label="业务员名称"  name="salespersonName">
          <a-input style="width: 100%" v-model:value="form.salespersonName" placeholder="业务员名称" />
        </a-form-item>
      <a-form-item label="金额"  name="amount">
        <a-input style="width: 100%" v-model:value="form.amount" placeholder="业务员名称" />
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
  import { reactive, ref, nextTick } from 'vue';
  import _ from 'lodash';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { salesOutboundApi } from '/@/api/vigorous/sales-outbound-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import DictSelect from "/@/components/support/dict-select/index.vue";
  import {f} from "vue3-tabs-chrome";

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
      salesBoundId: undefined, //主键
      billNo: undefined, //单据编号
    billStatus: undefined, //单据编号
      salesBoundDate: undefined, //出库日期
      customerCode: undefined, //客户编号
      salespersonName: undefined, //业务员编号
    amount:undefined
  };

  let form = reactive({ ...formDefault });

  const rules = {
      salesBoundId: [{ required: true, message: '主键 必填' }],
      billNo: [{ required: true, message: '单据编号 必填' }],
    billStatus: [{ required: true, message: '单据状态 必填' }],
      salesBoundDate: [{ required: true, message: '出库日期 必填' }],
    customerCode: [{ required: true, message: '客户编号 必填' }],
    salespersonName: [{ required: true, message: '业务员编号 必填' }],
    amount: [{ required: true, message: '金额 必填' }],
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
      if (form.salesBoundId) {
        await salesOutboundApi.update(form);
      } else {
        await salesOutboundApi.add(form);
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
