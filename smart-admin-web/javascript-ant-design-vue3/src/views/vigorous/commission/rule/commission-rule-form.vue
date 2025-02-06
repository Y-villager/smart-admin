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

      <a-form-item label="客户分组" class="smart-query-form-item">
        <SmartEnumSelect width="200px" v-model:value="form.customerGroup" enumName="CUSTOMER_GROUP_ENUM" placeholder="客户分组"/>
      </a-form-item>
      <a-form-item label="转交状态" class="smart-query-form-item">
        <SmartEnumSelect width="200px" v-model:value="form.transferStatus" enumName="IS_TRANSFERRED_ENUM" placeholder="转交状态（0自主开发，非0转交）"/>
      </a-form-item>
      <a-form-item label="提成类型" class="smart-query-form-item">
        <SmartEnumSelect width="200px" v-model:value="form.commissionType" enumName="COMMISSION_TYPE_ENUM" placeholder="提成类型（1业务 2管理）"/>
      </a-form-item>
      <a-form-item label="是否计算公式"  name="useDynamicFormula">
        <SmartEnumSelect width="200px" v-model:value="form.useDynamicFormula" enumName="SYSTEM_YES_NO" placeholder="提成类型（1业务 2管理）"/>
      </a-form-item>
        <a-form-item label="提成系数"  name="commissionRate">
          <a-input-number style="width: 100%" v-model:value="form.commissionRate" placeholder="提成系数" />
        </a-form-item>
        <a-form-item label="计算公式id"  name="formulaId">
          <a-input-number style="width: 100%" v-model:value="form.formulaId" placeholder="计算公式id" />
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
  import SmartEnumSelect from "/@/components/framework/smart-enum-select/index.vue";

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
      transferStatus: undefined, //转交状态（0自主开发，非0转交）
      customerGroup: undefined, //客户分组(1内贸 2外贸)
      commissionType: undefined, //提成类型（1业务 2管理）
      useDynamicFormula: 0, //是否计算公式（0否 1是）
      commissionRate: undefined, //提成系数
      formulaId: undefined, //计算公式id
      remark: undefined, //备注
  };

  let form = reactive({ ...formDefault });

  const rules = {
      transferStatus: [{ required: true, message: '转交状态 必填' }],
      customerGroup: [{ required: true, message: '客户分组 必填' }],
      commissionType: [{ required: true, message: '提成类型 必填' }],
      useDynamicFormula: [{ required: true, message: '是否计算公式 必填' }],
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
