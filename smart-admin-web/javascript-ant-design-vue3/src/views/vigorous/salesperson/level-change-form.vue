<!--
  * 业务员级别变动记录
  *
  * @Author:    yxz
  * @Date:      2025-01-07 08:58:41
  * @Copyright  (c)2024 yxz
-->
<template>
  <a-modal
      :title="form.salespersonId ? '编辑' : '添加'"
      :width="500"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
      <a-form-item label="业务员编号"  name="salespersonId">
        <a-input-number style="width: 100%" v-model:value="form.salespersonId" placeholder="业务员编号" disabled/>
      </a-form-item>
      <a-form-item label="先前级别"  name="oldLevel">
        <SalespersonLevelSelect width="100%" v-model:value="form.oldLevel" enumName="" placeholder="选择业务员级别" disabled/>
      </a-form-item>
      <a-form-item label="现在级别"  name="newLevel">
        <SalespersonLevelSelect width="100%" v-model:value="form.newLevel" enumName="" placeholder="选择业务员级别"/>
      </a-form-item>
      <a-form-item label="结束时间"  name="changeDate">
        <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.changeDate" style="width: 100%" placeholder="变动日期"/>
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
import SalespersonLevelSelect from "/@/components/vigorous/salesperson-level-select/index.vue";
import {salespersonLevelApi} from "/@/api/vigorous/salesperson-level-api.js";

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
  if (form.id){
    form.salespersonId = form.id
  }

  visibleFlag.value = true;
  nextTick(() => {
    formRef.value.clearValidate();
    if (rowData.salespersonLevelId != null){
      form.oldLevel = rowData.salespersonLevelId
    }
    console.log(form)
  });
}

function onClose() {
  Object.assign(form, formDefault);
  visibleFlag.value = false;
}

function formatDate(date) {
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  return `${year}-${month}-${day}`;
}

// ------------------------ 表单 ------------------------

// 组件ref
const formRef = ref();

const formDefault = {
  salespersonId: undefined, //业务员id
  oldLevel: undefined, //先前级别
  newLevel: undefined, //现在级别
  changeDate: formatDate(new Date()), //结束时间
  changeReason: undefined, //变动原因
};

let form = reactive({ ...formDefault });

const rules = {
  salespersonId: [{ required: true, message: '业务员编号 必填' }],
  newLevel: [{ required: true, message: '现在级别 必填' }],
  changeDate: [{ required: true, message: '变动日期 必填' }],
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
    await salespersonLevelApi.updateLevel(form);
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
