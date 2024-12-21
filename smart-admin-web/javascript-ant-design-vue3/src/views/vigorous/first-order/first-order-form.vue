<!--
  * 客户首单信息
  *
  * @Author:    yxz
  * @Date:      2024-12-12 14:50:26
  * @Copyright  (c)2024 yxz
-->
<template>
  <a-drawer
      :title="form.firstOrderId ? '编辑' : '添加'"
      :width="500"
      :open="visibleFlag"
      @close="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="客户编码"  name="customerId">
          <a-input style="width: 100%" v-model:value="form.customerId" placeholder="客户编码" />
        </a-form-item>
        <a-form-item label="业务员编码"  name="salespersonId">
          <a-input style="width: 100%" v-model:value="form.salespersonId" placeholder="业务员编码" />
        </a-form-item>
        <a-form-item label="首单编号"  name="billNo">
          <a-input style="width: 100%" v-model:value="form.billNo" placeholder="首单编号" />
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
  import { firstOrderApi } from '/@/api/vigorous/first-order-api';
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
      customerId: undefined, //客户编码
      salespersonId: undefined, //业务员编码
      billNo: undefined, //首单编号
  };

  let form = reactive({ ...formDefault });

  const rules = {
      customerId: [{ required: true, message: '客户编码 必填' }],
      salespersonId: [{ required: true, message: '业务员编码 必填' }],
      billNo: [{ required: true, message: '首单编号 必填' }],
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
      if (form.firstOrderId) {
        await firstOrderApi.update(form);
      } else {
        await firstOrderApi.add(form);
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
