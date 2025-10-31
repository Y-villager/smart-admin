<!--
  * 应收明细表
  *
  * @Author:    yxz
  * @Date:      2025-10-23 14:43:51
  * @Copyright  (c)2025 yxz
-->
<template>
  <a-modal
      :title="form.id ? '编辑' : '添加'"
      :width="500"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="物料编码"  name="materialCode">
          <a-input style="width: 100%" v-model:value="form.materialCode" placeholder="物料编码" />
        </a-form-item>
        <a-form-item label="物料名称"  name="materialName">
          <a-input style="width: 100%" v-model:value="form.materialName" placeholder="物料名称" />
        </a-form-item>
        <a-form-item label="应收单id"  name="originBillNo">
          <a-input-number style="width: 100%" v-model:value="form.originBillNo" placeholder="应收单id" />
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
  import { SmartLoading } from '/src/components/framework/smart-loading';
  import { receivablesDetailsApi } from '/src/api/vigorous/receivables-details-api';
  import { smartSentry } from '/src/lib/smart-sentry';

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
      materialCode: undefined, //物料编码
      materialName: undefined, //物料名称
      originBillNo: undefined, //应收单id
  };

  let form = reactive({ ...formDefault });

  const rules = {
      materialCode: [{ required: true, message: '物料编码 必填' }],
      materialName: [{ required: true, message: '物料名称 必填' }],
      originBillNo: [{ required: true, message: '应收单id 必填' }],
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
      if (form.id) {
        await receivablesDetailsApi.update(form);
      } else {
        await receivablesDetailsApi.add(form);
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
