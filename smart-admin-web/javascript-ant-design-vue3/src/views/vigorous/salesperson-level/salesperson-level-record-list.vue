<!--
  * 业务员级别变动记录
  *
  * @Author:    yxz
  * @Date:      2025-01-07 08:58:41
  * @Copyright  (c)2024 yxz
-->
<template>
    <!---------- 查询表单form begin ----------->
    <a-form class="smart-query-form">
        <a-row class="smart-query-form-row">
            <a-form-item label="业务员名称" class="smart-query-form-item">
                <a-input style="width: 200px" v-model:value="queryForm.salespersonName" placeholder="业务员名称" />
            </a-form-item>
            <a-form-item label="变动时间" class="smart-query-form-item">
                <a-range-picker v-model:value="queryForm.changeDate" :presets="defaultTimeRanges" style="width: 200px" @change="onChangeCreateTime" />
            </a-form-item>
            <a-form-item class="smart-query-form-item">
                <a-button type="primary" @click="onSearch">
                    <template #icon>
                        <SearchOutlined />
                    </template>
                    查询
                </a-button>
                <a-button @click="resetQuery" class="smart-margin-left10">
                    <template #icon>
                        <ReloadOutlined />
                    </template>
                    重置
                </a-button>
            </a-form-item>
        </a-row>
    </a-form>
    <!---------- 查询表单form end ----------->

    <a-card size="small" :bordered="false" :hoverable="true">
        <!---------- 表格操作行 begin ----------->
        <a-row class="smart-table-btn-block">
            <div class="smart-table-operate-block">
                <a-button @click="showForm" type="primary">
                    <template #icon>
                        <PlusOutlined />
                    </template>
                    新建
                </a-button>
            </div>
            <div class="smart-table-setting-block">
                <TableOperator v-model="columns" :tableId="null" :refresh="queryData" />
            </div>
        </a-row>
        <!---------- 表格操作行 end ----------->

        <!---------- 表格 begin ----------->
        <a-table
            size="small"
            :dataSource="tableData"
            :columns="columns"
            rowKey="id"
            bordered
            :loading="tableLoading"
            :pagination="false"
        >
            <template #bodyCell="{ text, record, column }">

	    <!-- 有图片预览时 注释解开并把下面的'picture'修改成自己的图片字段名即可 -->
              <!-- <template v-if="column.dataIndex === 'picture'">
                  <FilePreview :fileList="text" type="picture" />
                </template> -->

	    <!-- 使用字典时 注释解开并把下面的'dict'修改成自己的字典字段名即可 有多个字典字段就复制多份同理修改 不然不显示字典 -->
              <!-- 方便修改tag的颜色 orange green purple success processing error default warning -->
              <template v-if="column.dataIndex === 'oldLevel'">
                <generic-dto :selected-id="text" :list="salespersonLevelList"/>
              </template>
              <template v-if="column.dataIndex === 'newLevel'">
                <generic-dto :selected-id="text" :list="salespersonLevelList"/>
              </template>

                <template v-if="column.dataIndex === 'action'">
                    <div class="smart-table-operate">
                        <a-button @click="showForm(record)" type="link">编辑</a-button>
                        <a-button @click="onDelete(record)" danger type="link" v-privilege="'salespersonLevelRecord:delete'">删除</a-button>
                    </div>
                </template>
            </template>
        </a-table>
        <!---------- 表格 end ----------->

        <div class="smart-query-table-page">
            <a-pagination
                showSizeChanger
                showQuickJumper
                show-less-items
                :pageSizeOptions="PAGE_SIZE_OPTIONS"
                :defaultPageSize="queryForm.pageSize"
                v-model:current="queryForm.pageNum"
                v-model:pageSize="queryForm.pageSize"
                :total="total"
                @change="queryData"
                @showSizeChange="queryData"
                :show-total="(total) => `共${total}条`"
            />
        </div>

        <SalespersonLevelRecordForm  ref="formRef" @reloadList="queryData"/>

    </a-card>
</template>
<script setup>
    import { reactive, ref, onMounted } from 'vue';
    import { salespersonLevelRecordApi } from '/@/api/vigorous/salesperson-level-record-api';
    import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
    import { smartSentry } from '/@/lib/smart-sentry';
    import TableOperator from '/@/components/support/table-operator/index.vue';
    import SalespersonLevelRecordForm from './salesperson-level-record-form.vue';
    import { defaultTimeRanges } from '/@/lib/default-time-ranges';
    import {message, Modal} from "ant-design-vue";
    import {SmartLoading} from "/@/components/framework/smart-loading/index.js";
    import {salespersonLevelApi} from "/@/api/vigorous/salesperson-level-api.js";
    import GenericDto from "/@/components/vigorous/GenericDto/index.vue";
    //import FilePreview from '/@/components/support/file-preview/index.vue'; // 图片预览组件

    // ---------------------------- 表格列 ----------------------------

    const columns = ref([
        {
            title: '业务员',
            dataIndex: 'salespersonName',
            ellipsis: true,
        },
        {
            title: '先前级别',
            dataIndex: 'oldLevel',
            ellipsis: true,
        },
        {
            title: '现在级别',
            dataIndex: 'newLevel',
            ellipsis: true,
        },
        {
            title: '变动日期',
            dataIndex: 'changeDate',
            ellipsis: true,
        },
        {
            title: '变动原因',
            dataIndex: 'changeReason',
            ellipsis: true,
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            ellipsis: true,
        },
        {
            title: '操作',
            dataIndex: 'action',
            fixed: 'right',
            width: 90,
        },
    ]);

    // ---------------------------- 查询数据表单和方法 ----------------------------

    const queryFormState = {
        salespersonName: undefined, //业务员名称
        changeDate: [], //变动时间
        changeDateBegin: undefined, //变动时间 开始
        changeDateEnd: undefined, //变动时间 结束
        pageNum: 1,
        pageSize: 10,
    };
    // 查询表单form
    const queryForm = reactive({ ...queryFormState });
    // 表格加载loading
    const tableLoading = ref(false);
    // 表格数据
    const tableData = ref([]);
    // 总数
    const total = ref(0);

    // 重置查询条件
    function resetQuery() {
        let pageSize = queryForm.pageSize;
        Object.assign(queryForm, queryFormState);
        queryForm.pageSize = pageSize;
        queryData();
    }

    // 搜索
    function onSearch(){
      queryForm.pageNum = 1;
      queryData();
    }

    // 查询数据
    async function queryData() {
        tableLoading.value = true;
        try {
            let queryResult = await salespersonLevelRecordApi.queryPage(queryForm);
            tableData.value = queryResult.data.list;
            total.value = queryResult.data.total;
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            tableLoading.value = false;
        }
    }

    function onChangeCreateTime(dates, dateStrings){
        queryForm.changeDateBegin = dateStrings[0];
        queryForm.changeDateEnd = dateStrings[1];
    }


    // 使用 onMounted 加载数据
    onMounted(() => {
      queryData();
      getAllSalespersonLevel();
    });
    // ----------------------------  ----------------------------
    const salespersonLevelList = ref([]);
    async function getAllSalespersonLevel() {
      await salespersonLevelApi.queryList().then(res =>{
        console.log('---------------------level-record')
        console.log(res)
        salespersonLevelList.value = res.data
      })
    }

    // ---------------------------- 添加/修改 ----------------------------
    const formRef = ref();

    function showForm(data) {
        formRef.value.show(data);
    }
    // ---------------------------- 单个删除 ----------------------------
    //确认删除
    function onDelete(data){
      Modal.confirm({
        title: '提示',
        content: '确定要删除选吗?',
        okText: '删除',
        okType: 'danger',
        onOk() {
          requestDelete(data);
        },
        cancelText: '取消',
        onCancel() {},
      });
    }

    //请求删除
    async function requestDelete(data){
      SmartLoading.show();
      try {
        let deleteForm = {
          goodsIdList: selectedRowKeyList.value,
        };
        await salespersonLevelRecordApi.delete(data.id);
        message.success('删除成功');
        queryData();
      } catch (e) {
        smartSentry.captureError(e);
      } finally {
        SmartLoading.hide();
      }
    }

    // ---------------------------- 批量删除 ----------------------------

    // 选择表格行
    const selectedRowKeyList = ref([]);

    function onSelectChange(selectedRowKeys) {
      selectedRowKeyList.value = selectedRowKeys;
    }

    // 批量删除
    function confirmBatchDelete() {
      Modal.confirm({
        title: '提示',
        content: '确定要批量删除这些数据吗?',
        okText: '删除',
        okType: 'danger',
        onOk() {
          requestBatchDelete();
        },
        cancelText: '取消',
        onCancel() {},
      });
    }

    //请求批量删除
    async function requestBatchDelete() {
      try {
        SmartLoading.show();
        await salespersonLevelRecordApi.batchDelete(selectedRowKeyList.value);
        message.success('删除成功');
        await queryData();
      } catch (e) {
        smartSentry.captureError(e);
      } finally {
        SmartLoading.hide();
      }
    }

</script>
