<template>
  <a-select
      v-model:value="value"
      style="width: 100%"
      mode="multiple"
      placeholder="请选择"
      @change="change"
      :options="formattedMonths.map((item) => ({ value: item }))"
  >
    <template #dropdownRender="{ menuNode: menu }">
      <div class="select-time" style="padding: 4px 8px; cursor: pointer" @mousedown="(e) => e.preventDefault()">
        <a-button @click="handleLeftButtonClick">左</a-button>
        <div>{{ currentYear }}</div>
        <a-button @click="handleRightButtonClick">右</a-button>
      </div>
      <a-divider style="margin: 4px 0" />
      <v-nodes :vnodes="menu" />
    </template>
  </a-select>
</template>

<script>
import { defineComponent, ref, computed, watch } from 'vue';
import dayjs from 'dayjs';
import moment from "moment";

export default defineComponent({
  props: {
    types: {
      type: String,
      required: true,
      default: "1",
    },
    format: {
      type: String,
      default: undefined, // default format, you can change this as needed
    },
  },
  components: {
    VNodes: (_, { attrs }) => {
      return attrs.vnodes;
    },
  },
  setup(props, { emit }) {
    const currentYear = ref(dayjs().format('YYYY'));
    const value = ref([]); // 用于存储选中的值

    // 计算季度或月份的选项
    const formattedMonths = computed(() => {
      let monthNames = null;
      if (props.types === "1") {
        monthNames = ['第一季度', '第二季度', '第三季度', '第四季度'];
      } else {
        monthNames = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];
      }
      return monthNames.map((month) => `${currentYear.value}-${month}`);
    });
    const monthNames = props.types === "1"
        ? ['一季度', '二季度', '三季度', '四季度']
        : ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];


    // 左切换按钮点击事件处理程序
    function handleLeftButtonClick() {
      currentYear.value = dayjs(currentYear.value).subtract(1, 'year').format('YYYY');
      value.value = []; // 清空选择
    }

    // 右切换按钮点击事件处理程序
    function handleRightButtonClick() {
      currentYear.value = dayjs(currentYear.value).add(1, 'year').format('YYYY');
      value.value = []; // 清空选择
    }

    // 处理选择变化并返回日期范围
    function change(selectedValues) {
      // 定义一个空数组，用于存储转换后的日期范围
      const dateRanges = selectedValues.map((value) => {
        const [year, month] = value.split('-');  // 拆分 year 和 month
        let startDate, endDate;

        if (props.types === "1") {
          // 处理季度逻辑
          const quarterIndex = monthNames.indexOf(month); // 获取季度的索引
          if (quarterIndex >= 0) {
            // 计算季度的开始和结束日期
            const startMonth = (quarterIndex * 3) + 1;
            const endMonth = (quarterIndex + 1) * 3;

            startDate = dayjs(`${year}-${startMonth < 10 ? '0' + startMonth : startMonth}-01`).startOf('month').toDate();
            endDate = dayjs(`${year}-${endMonth < 10 ? '0' + endMonth : endMonth}-01`).endOf('month').toDate();
          }
        } else {
          // 处理月份逻辑
          const monthIndex = parseInt(month.replace(/[^\d]/g, "")); // 获取数字月份，如 '1月' -> 1
          startDate = dayjs(`${year}-${monthIndex < 10 ? '0' + monthIndex : monthIndex}-01`).startOf('month').toDate();
          endDate = dayjs(`${year}-${monthIndex < 10 ? '0' + monthIndex : monthIndex}-01`).endOf('month').toDate();
        }

        if (props.format !== undefined){
          const formattedStartDate = moment(startDate).format(props.format)
          const formattedEndDate = moment(endDate).format(props.format)
          return { startDate: formattedStartDate, endDate: formattedEndDate };
        }

        return { startDate: startDate, endDate: endDate };
      });

      // 向父组件发送选择的日期范围
      emit('changeSelect', dateRanges);
    }

    // 监听 props.types 的变化
    watch(() => props.types, () => {
      // 当 types 改变时，清空选中的数据
      value.value = [];
    });

    return {
      value,
      formattedMonths,
      currentYear,
      handleLeftButtonClick,
      handleRightButtonClick,
      change,
    };
  },
});
</script>

<style lang="less">
.select-time {
  display: flex;
  padding: 3px 5px;
  box-sizing: border-box;
  align-items: center;
  justify-content: space-between;
}
</style>
