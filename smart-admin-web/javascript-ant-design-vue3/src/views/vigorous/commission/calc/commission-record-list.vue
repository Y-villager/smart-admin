<!--
  * 业务提成记录
  *
  * @Author:    yxz
  * @Date:      2025-01-12 15:25:35
  * @Copyright  (c)2024 yxz
-->
<template>
    <!---------- 查询表单form begin ----------->
    <a-form class="smart-query-form">
        <a-row class="smart-query-form-row">
          <a-form-item label="单据编号" class="smart-query-form-item">
            <a-input style="width: 200px" v-model:value="queryForm.salesBillNo" placeholder="销售出库-单据编号" />
          </a-form-item>
            <a-form-item label="业务员" class="smart-query-form-item">
                <a-input style="width: 200px" v-model:value="queryForm.salespersonName" placeholder="业务员" />
            </a-form-item>
            <a-form-item label="客户名称" class="smart-query-form-item">
                <a-input style="width: 200px" v-model:value="queryForm.customerName" placeholder="客户名称" />
            </a-form-item>
            <a-form-item label="销售出库日期" class="smart-query-form-item">
                <a-range-picker v-model:value="queryForm.orderDate" :presets="defaultTimeRanges" style="width: 200px" @change="onChangeOrderDate" />
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
                <a-button @click="confirmBatchDelete" type="primary" danger :disabled="selectedRowKeyList.length == 0">
                    <template #icon>
                        <DeleteOutlined />
                    </template>
                    批量删除
                </a-button>
<!--              <a-button @click="showImportModal" type="primary" v-privilege="'commissionRecord:importCommissionRecord'">-->
<!--                <template #icon>-->
<!--                  <ImportOutlined />-->
<!--                </template>-->
<!--                导入-->
<!--              </a-button>-->

              <a-button @click="onExportCommissionRecord" :loading="exportLoading" type="primary" v-privilege="'commissionRecord:exportCommissionRecord'">
                <template #icon>
                  <ExportOutlined />
                </template>
                导出
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
            rowKey="commissionId"
            bordered
            :loading="tableLoading"
            :pagination="false"
            :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
            :scroll="{ x: 'max-content', y: 500 }"
        >
            <template #bodyCell="{ text, record, column }">
                <template v-if="column.dataIndex === 'action'">
                    <div class="smart-table-operate">
                        <a-button @click="showForm(record)" type="link">编辑</a-button>
                        <a-button @click="onDelete(record)" danger type="link">删除</a-button>
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

        <CommissionRecordForm  ref="formRef" @reloadList="queryData"/>

      <a-modal v-model:open="importModalShowFlag" title="导入" @onCancel="hideImportModal" @ok="hideImportModal">
        <div style="text-align: center; width: 400px; margin: 0 auto">
          <div id="app">
            <span>导入模式：</span>

            <!-- 绑定 radio 按钮 -->
            <label>
              <input type="radio" v-model="importMode" value="1"/> 追加
            </label>
            <label>
              <input type="radio" v-model="importMode" value="0"/> 覆盖
            </label>
          </div>
          <br/>
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
              选择文件
            </a-button>
          </a-upload>

          <br />
          <a-button @click="onImportCommissionRecord">
            <ImportOutlined />
            开始导入
          </a-button>
          <br/>
          <a-button type="text" @click="downloadFailedData">下载失败数据</a-button>
        </div>
      </a-modal>

    </a-card>
</template>
<script setup>
    import { reactive, ref, onMounted } from 'vue';
    import { message, Modal } from 'ant-design-vue';
    import { SmartLoading } from '/@/components/framework/smart-loading';
    import { commissionRecordApi } from '/@/api/vigorous/commission-record-api';
    import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
    import { smartSentry } from '/@/lib/smart-sentry';
    import TableOperator from '/@/components/support/table-operator/index.vue';
    import {excelApi} from "/@/api/vigorous/excel-api.js";

    import { defaultTimeRanges } from '/@/lib/default-time-ranges';
    import CommissionRecordForm from './commission-record-form.vue';
    import SmartEnumSelect from "/@/components/framework/smart-enum-select/index.vue";
    //import FilePreview from '/@/components/support/file-preview/index.vue'; // 图片预览组件

    // ---------------------------- 表格列 ----------------------------

    const columns = ref([
        {
            title: '销售出库-单据编号',
            dataIndex: 'salesBillNo',
            ellipsis: true,
            width: '160px'
        },
      {
        title: '业务日期',
        dataIndex: 'orderDate',
        ellipsis: true,
        width: '100px'
      },
        {
          title: '业务金额',
          dataIndex: 'salesAmount',
          ellipsis: true,
          width: '100px'
        },
        {
          title: '应收-币别',
          dataIndex: 'currencyType',
          ellipsis: true,
          width: '80px'
        },
        {
            title: '业务员',
            dataIndex: 'salespersonName',
            ellipsis: true,
            width: '80px'
        },
        {
            title: '客户',
            dataIndex: 'customerName',
            ellipsis: true,
            width: '150px'
        },
      {
        title: '首单日期',
        dataIndex: 'adjustedFirstOrderDate',
        ellipsis: true,
        width: '100px'
      },
      {
        title: '客户合作年数',
        dataIndex: 'customerYear',
        ellipsis: true,
        width: '100px'
      },
      {
        title: '客户年份系数',
        dataIndex: 'customerYearRate',
        ellipsis: true,
        width: '100px'
      },
      {
        title: '业务提成比例(%)',
        dataIndex: 'businessCommissionRate',
        ellipsis: true,
        width: '130px'

      },
        {
            title: '业务提成金额',
            dataIndex: 'businessCommissionAmount',
            ellipsis: true,
            width: '110px'
        },
      {
        title: '管理提成比例(%)',
        dataIndex: 'managementCommissionRate',
        ellipsis: true,
        width: '130px'
      },
      {
        title: '管理提成金额',
        dataIndex: 'managementCommissionAmount',
        ellipsis: true,
        width: '110px'
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
      salesBillNo: undefined,
        salespersonName: undefined, //业务员
        customerName: undefined, //客户名称
        orderDate: [], //销售出库日期
        orderDateBegin: undefined, //销售出库日期 开始
        orderDateEnd: undefined, //销售出库日期 结束
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
            let queryResult = await commissionRecordApi.queryPage(queryForm);
            tableData.value = queryResult.data.list;
            total.value = queryResult.data.total;
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            tableLoading.value = false;
        }
    }

    function onChangeOrderDate(dates, dateStrings){
        queryForm.orderDateBegin = dateStrings[0];
        queryForm.orderDateEnd = dateStrings[1];
    }


    onMounted(queryData);

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
            await commissionRecordApi.delete(data.commissionId);
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
            await commissionRecordApi.batchDelete(selectedRowKeyList.value);
            message.success('删除成功');
            queryData();
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            SmartLoading.hide();
        }
    }

// ------------------------------- 导出和导入 ---------------------------------
// 导入弹窗
const importModalShowFlag = ref(false);

const fileList = ref([]);

// 显示导入
function showImportModal() {
  fileList.value = [];
  importModalShowFlag.value = true;
}

// 关闭 导入
function hideImportModal() {
  importModalShowFlag.value = false;
}

// 导出excel文件
    const exportLoading = new ref(false);
async function onExportCommissionRecord() {
  exportLoading.value = true
  await commissionRecordApi.exportCommissionRecord(queryForm);
  exportLoading.value = false;
}

function handleRemove(file) {
  const index = fileList.value.indexOf(file);
  const newFileList = fileList.value.slice();
  newFileList.splice(index, 1);
  fileList.value = newFileList;
}

function beforeUpload(file) {
  fileList.value = [...(fileList.value || []), file];
  return false;
}
// 下载模板
function downloadExcel() {
}

const importMode = ref(1)
// 导入文件
async function onImportCommissionRecord() {
  const formData = new FormData();
  fileList.value.forEach((file) => {
    formData.append('file', file.originFileObj);
    formData.append('mode', importMode.value);
  });

  SmartLoading.show();
  try {
    let res = await commissionRecordApi.importCommissionRecord(formData);
    failed_import_data.value = res.data;
    message.success(res.msg);
  } catch (e) {
    smartSentry.captureError(e);
  } finally {
    SmartLoading.hide();
  }
}

// 下载导入失败数据
const failed_import_data = ref();
function downloadFailedData(){
  if (failed_import_data.value != null){
    try{
      excelApi.downloadFailedImportData();
    }catch (e){
      smartSentry.captureError(e)
    }
  }else {
    message.error("当前没有导入失败数据")
  }
}
</script>
