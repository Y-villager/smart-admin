<!--
  * 提成规则
  *
  * @Author:    yxz
  * @Date:      2024-12-23 16:14:00
  * @Copyright  (c)2024 yxz
-->
<template>
  <a-drawer
      :title="form.ruleId ? '编辑' : '添加'"
      :width="500"
      :open="visibleFlag"
      @close="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="币种"  name="currencyType">
          <DictSelect width="100%" v-model:value="form.currencyType" keyCode="CURRENCY_TYPE" placeholder="币种"/>
        </a-form-item>
        <a-form-item label="业务员级别"  name="salespersonLevelId">
          <SalespersonLevelSelect width="100%" v-model:value="form.salespersonLevelId" enumName="" placeholder="业务员级别id"/>
        </a-form-item>
        <a-form-item label="首单比例"  name="firstOrderRate">
          <a-input-number style="width: 100%" v-model:value="form.firstOrderRate" placeholder="首单比例" />
        </a-form-item>
        <a-form-item label="基础比例"  name="baseRate">
          <a-input-number style="width: 100%" v-model:value="form.baseRate" placeholder="基础比例" />
        </a-form-item>
        <a-form-item label="备注"  name="remark">
          <a-input style="width: 100%" v-model:value="form.remark" placeholder="备注" />
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
  import { commissionRuleApi } from '/@/api/vigorous/commission-rule-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import DictSelect from '/@/components/support/dict-select/index.vue';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
  import SalespersonLevelSelect from "/@/components/vigorous/salesperson-level-select/index.vue";

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
      currencyType: undefined, //币种
      salespersonLevelId: undefined, //业务员级别id
      firstOrderRate: undefined, //首单比例
      baseRate: undefined, //基础比例
      remark: undefined, //备注
  };

  let form = reactive({ ...formDefault });

  const rules = {
      currencyType: [{ required: true, message: '币种 必填' }],
      salespersonLevelId: [{ required: true, message: '业务员级别 必填' }],
      firstOrderRate: [{ required: true, message: '首单比例 必填' }],
      baseRate: [{ required: true, message: '基础比例 必填' }],
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
      if (form.ruleId) {
        await commissionRuleApi.update(form);
      } else {
        await commissionRuleApi.add(form);
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
