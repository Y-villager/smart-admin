<!--
  * 业务提成记录
  *
  * @Author:    yxz
  * @Date:      2025-01-12 15:25:35
  * @Copyright  (c)2024 yxz
-->
<template>
  <a-drawer
      :title="form.commissionId ? '编辑' : '添加'"
      :width="500"
      :open="visibleFlag"
      @close="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="业务员id"  name="salespersonId">
          <a-input style="width: 100%" v-model:value="form.salespersonId" placeholder="业务员id" />
        </a-form-item>
        <a-form-item label="客户id"  name="customerId">
          <a-input style="width: 100%" v-model:value="form.customerId" placeholder="客户id" />
        </a-form-item>
        <a-form-item label="提成类型"  name="commissionType">
          <SmartEnumSelect width="100%" v-model:value="form.commissionType" enumName="" placeholder="提成类型(0业务1管理）"/>
        </a-form-item>
        <a-form-item label="提成金额"  name="amount">
          <a-input-number style="width: 100%" v-model:value="form.amout" placeholder="提成金额" />
        </a-form-item>
        <a-form-item label="销售出库id"  name="salesOutboundId">
          <a-input-number style="width: 100%" v-model:value="form.salesOutboundId" placeholder="销售出库id" />
        </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">关闭</a-button>
      </a-space>
    </template>
  </a-drawer>
</template>
<script setup>
  import { reactive, ref, nextTick } from 'vue';
  import _ from 'lodash';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { commissionRecordApi } from '/@/api/vigorous/commission-record-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';

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
      salespersonId: undefined, //业务员id
      customerId: undefined, //客户id
      commissionType: undefined, //提成类型(0业务1管理）
      amout: undefined, //提成金额
      salesOutboundId: undefined, //销售出库id
  };

  let form = reactive({ ...formDefault });

  const rules = {
      salespersonId: [{ required: true, message: '业务员id 必填' }],
      customerId: [{ required: true, message: '客户id 必填' }],
      commissionType: [{ required: true, message: '提成类型(0业务1管理） 必填' }],
      salesOutboundId: [{ required: true, message: '销售出库id 必填' }],
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
      if (form.commissionId) {
        await commissionRecordApi.update(form);
      } else {
        await commissionRecordApi.add(form);
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
