<!--
  * 业务员级别变动记录
  *
  * @Author:    yxz
  * @Date:      2025-01-07 08:58:41
  * @Copyright  (c)2024 yxz
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
      <a-form-item label="业务员id"  name="salespersonId">
        <a-input-number style="width: 100%" v-model:value="form.salespersonId" placeholder="业务员id" />
      </a-form-item>
      <a-form-item label="先前级别"  name="oldLevel">
        <a-input style="width: 100%" v-model:value="form.oldLevel" placeholder="先前级别" />
      </a-form-item>
      <a-form-item label="现在级别"  name="newLevel">
        <a-input style="width: 100%" v-model:value="form.newLevel" placeholder="现在级别" />
      </a-form-item>
      <a-form-item label="开始时间"  name="startDate">
        <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.startDate" style="width: 100%" placeholder="开始时间"/>
      </a-form-item>
      <a-form-item label="结束时间"  name="endDate">
        <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.endDate" style="width: 100%" placeholder="结束时间"/>
      </a-form-item>
      <a-form-item label="变动原因"  name="changeReason">
        <a-input style="width: 100%" v-model:value="form.changeReason" placeholder="变动原因" />
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
import { salespersonLevelRecordApi } from '/@/api/vigorous/salesperson-level-record-api';
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
  salespersonId: undefined, //业务员id
  oldLevel: undefined, //先前级别
  newLevel: undefined, //现在级别
  startDate: undefined, //开始时间
  endDate: undefined, //结束时间
  changeReason: undefined, //变动原因
};

let form = reactive({ ...formDefault });

const rules = {
  salespersonId: [{ required: true, message: '业务员id 必填' }],
  oldLevel: [{ required: true, message: '先前级别 必填' }],
  newLevel: [{ required: true, message: '现在级别 必填' }],
  startDate: [{ required: true, message: '开始时间 必填' }],
  changeReason: [{ required: true, message: '变动原因 必填' }],
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
      await salespersonLevelRecordApi.update(form);
    } else {
      await salespersonLevelRecordApi.add(form);
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
