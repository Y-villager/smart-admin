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
                <a-range-picker v-model:value="queryForm.createTime" :presets="defaultTimeRanges" style="width: 200px" @change="onChangeCreateTime" />
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
              <!-- <template v-if="column.dataIndex === 'dict'">
                <a-tag color="cyan">
                  {{ text && text.length > 0 ? text.map((e) => e.valueName).join(',') : '暂无' }}
                </a-tag>
              </template> -->

                <template v-if="column.dataIndex === 'action'">
                    <div class="smart-table-operate">
                        <a-button @click="showForm(record)" type="link">编辑</a-button>
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

      <a-modal v-model:open="importModalShowFlag" title="导入" @onCancel="hideImportModal" @ok="hideImportModal">
        <div style="text-align: center; width: 400px; margin: 0 auto">
          <a-button @click="downloadExcel">
            <download-outlined />
            第一步：下载模板
          </a-button>
          <br />
          <br />
          <a-upload
              v-model:fileList="fileList"
              name="file"
              :multiple="false"
              action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
              accept=".xls,.xlsx"
              :before-upload="beforeUpload"
              @remove="handleRemove"
          >
            <a-button>
              <upload-outlined />
              第二步：选择文件
            </a-button>
          </a-upload>

          <br />
          <a-button @click="onImportSalespersonLevelRecord">
            <ImportOutlined />
            第三步：开始导入
          </a-button>
        </div>
      </a-modal>

    </a-card>
</template>
<script setup>
    import { reactive, ref, onMounted } from 'vue';
    import { message, Modal } from 'ant-design-vue';
    import { SmartLoading } from '/@/components/framework/smart-loading';
    import { salespersonLevelRecordApi } from '/@/api/vigorous/salesperson-level-record-api';
    import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
    import { smartSentry } from '/@/lib/smart-sentry';
    import TableOperator from '/@/components/support/table-operator/index.vue';
    import SalespersonLevelRecordForm from './salesperson-level-record-form.vue';
    import { defaultTimeRanges } from '/@/lib/default-time-ranges';
    //import FilePreview from '/@/components/support/file-preview/index.vue'; // 图片预览组件

    // ---------------------------- 表格列 ----------------------------

    const columns = ref([
        {
            title: '业务员',
            dataIndex: 'salespersonId',
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
            title: '开始时间',
            dataIndex: 'startDate',
            ellipsis: true,
        },
        {
            title: '结束时间',
            dataIndex: 'endDate',
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
        createTime: [], //变动时间
        createTimeBegin: undefined, //变动时间 开始
        createTimeEnd: undefined, //变动时间 结束
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
        queryForm.createTimeBegin = dateStrings[0];
        queryForm.createTimeEnd = dateStrings[1];
    }


    onMounted(queryData);

    // ---------------------------- 添加/修改 ----------------------------
    const formRef = ref();

    function showForm(data) {
        formRef.value.show(data);
    }

</script>
