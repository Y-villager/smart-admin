<!--
  * 业务员级别 下拉选择框
  *
-->
<template>
  <a-select
      v-model:value="selectValue"
      :style="`width: ${width}`"
      :placeholder="props.placeholder"
      :showSearch="true"
      :allowClear="true"
      :size="size"
      @change="handleChange"
      :disabled="disabled"
      optionFilterProp="label"
  >
    <a-select-option v-for="item in dataList" :key="item.id" :label="item.name">
      {{ item.name }}
    </a-select-option>
  </a-select>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import {salespersonApi} from "/@/api/vigorous/salesperson/salesperson-api.js";

const props = defineProps({
  value: [Number, String],
  width: {
    type: String,
    default: '200px',
  },
  placeholder: {
    type: String,
    default: '请选择',
  },
  size: {
    type: String,
    default: 'default',
  },
  disabled: {
    type: Boolean,
    default: false,
  },
});

// ------------------选中 相关事件-----------------
const emit = defineEmits(['update:value', 'change']);

const selectValue = ref(props.value);

// 箭头value变化
// 监听 props.value 变化
watch(
    () => props.value,
    (newValue) => {
      selectValue.value = newValue;
    }
);

function handleChange(value) {
  emit('update:value', value);
  emit('change', value);
}

// ------------------查询列表数据-----------------
const dataList = ref([]);
async function queryData() {
  let res = await salespersonApi.getAllSalesperson();
  dataList.value = res.data;
}

onMounted(queryData);

</script>