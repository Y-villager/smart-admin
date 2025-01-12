<template>
  <div>
    <!-- 显示对应id的数据名称 -->
    <span>{{ getNameById(selectedId) }}</span>
  </div>
</template>

<script>
export default {
  name: "GenericDto",
  props: {
    // 传入的列表数据
    list: {
      type: Array,
      required: true
    },
    // 需要显示的id
    selectedId: {
      type: Number,
      required: true
    }
  },
  methods: {
    // 根据id查找对应的name
    getNameById(id) {
      const item = this.list.find(item => item.id === id);
      return item ? item.name : "未知"; // 如果没有找到，显示“未知”
    }
  }
};
</script>
