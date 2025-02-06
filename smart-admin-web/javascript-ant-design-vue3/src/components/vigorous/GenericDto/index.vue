<template>
  <div>
    <!-- 显示对应id的数据名称 -->
    <span>{{ getNameById(selectedId) }}</span>
  </div>
</template>

<script setup>
import { defineProps } from 'vue';

// 传入的props
const props = defineProps({
  list: {
    type: Array,
    required: true
  },
  selectedId: {
    type: Number,
    required: true
  }
});

// 根据id查找对应的name
const getNameById = (id) => {
  console.log(props.list);
  const item = props.list.find(item => item.id === id);
  return item ? item.name : "未知"; // 如果没有找到，显示“未知”
};
</script>
