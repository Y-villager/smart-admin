<!--
  * 业务员
  *
  * @Author:    yxz
  * @Date:      2024-12-16 10:56:45
  * @Copyright  (c)2024 yxz
-->
<template>
  <a-drawer
      :title="form.id ? '编辑' : '添加'"
      :width="100"
      :open="visibleFlag"
      @close="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="业务员编码"  name="salespersonCode">
          <a-input style="width: 100%" v-model:value="form.salespersonCode" placeholder="业务员编码" />
        </a-form-item>
        <a-form-item label="业务员名称"  name="salespersonName">
          <a-input style="width: 100%" v-model:value="form.salespersonName" placeholder="业务员名称" />
        </a-form-item>
        <a-form-item label="部门编码"  name="departmentId">
          <a-input-number style="width: 100%" v-model:value="form.departmentId" placeholder="部门编码" />
        </a-form-item>
        <a-form-item label="级别编码"  name="salespersonLevelId">
          <a-input-number style="width: 100%" v-model:value="form.salespersonLevelId" placeholder="级别编码" />
        </a-form-item>
        <a-form-item label="上级id"  name="parentId">
          <a-input-number style="width: 100%" v-model:value="form.parentId" placeholder="上级id" />
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
  import { salespersonApi } from '/@/api/vigorous/salesperson/salesperson-api';
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
      salespersonCode: undefined, //业务员编码
      salespersonName: undefined, //业务员名称
      departmentId: undefined, //部门编码
      salespersonLevelId: undefined, //级别编码
      parentId: undefined, //上级id
  };

  let form = reactive({ ...formDefault });

  const rules = {
      salespersonCode: [{ required: true, message: '业务员编码 必填' }],
      salespersonName: [{ required: true, message: '业务员名称 必填' }],
      departmentId: [{ required: true, message: '部门编码 必填' }],
      salespersonLevelId: [{ required: true, message: '级别编码 必填' }],
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
        await salespersonApi.update(form);
      } else {
        await salespersonApi.add(form);
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
