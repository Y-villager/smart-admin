<!--
  * 业务员级别
  *
  * @Author:    yxz
  * @Date:      2024-12-14 16:07:14
  * @Copyright  (c)2024 yxz
-->
<template>
  <a-modal
      :title="form.salespersonLevelId ? '编辑' : '添加'"
      :width="500"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="业务员级别名称"  name="salespersonLevelName">
          <a-input style="width: 100%" v-model:value="form.salespersonLevelName" placeholder="业务员级别名称" />
        </a-form-item>
        <a-form-item label="提成比例"  name="commissionRate">
          <a-input-number style="width: 100%" v-model:value="form.commissionRate" placeholder="提成比例" />
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
  import { salespersonLevelApi } from '/@/api/vigorous/salesperson-level-api';
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
      salespersonLevelName: undefined, //业务员级别名称
      commissionRate: undefined, //提成比例
  };

  let form = reactive({ ...formDefault });

  const rules = {
      salespersonLevelName: [{ required: true, message: '业务员级别名称 必填' }],
      commissionRate: [{ required: true, message: '提成比例 必填' }],
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
      if (form.salespersonLevelId) {
        await salespersonLevelApi.update(form);
      } else {
        await salespersonLevelApi.add(form);
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
