<!--
  * 应收单
  *
  * @Author:    yxz
  * @Date:      2024-12-12 14:46:31
  * @Copyright  yxz
-->
<template>
  <!---------- 查询表单form begin ----------->
  <a-form class="smart-query-form">
    <a-row class="smart-query-form-row">
      <a-form-item label="客户名称" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.customerName" placeholder="客户名称" />
      </a-form-item>
      <a-form-item label="销售员名称" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.salespersonName" placeholder="销售员名称" />
      </a-form-item>
      <a-form-item label="币别" class="smart-query-form-item">
        <DictSelect width="200px" v-model:aria-valuemax="queryForm.currencyType" keyCode="CURRENCY_TYPE" placeholder="币别"/>
      </a-form-item>
      <a-form-item label="收款日期" class="smart-query-form-item">
        <a-range-picker v-model:value="queryForm.receivablesDate" :presets="defaultTimeRanges" style="width: 240px" @change="onChangeReceivablesDate" />
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

        <a-button @click="confirmBatchDelete" type="primary" danger :disabled="selectedRowKeyList.length == 0">
          <template #icon>
            <DeleteOutlined />
          </template>
          批量删除
        </a-button>
        <a-button @click="showImportModal" type="primary" v-privilege="'receivables:importReceivables'">
          <template #icon>
            <ImportOutlined />
          </template>
          导入
        </a-button>

        <a-button @click="onExportReceivables" type="primary" v-privilege="'receivables:exportReceivables'">
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
        rowKey="receivablesId"
        bordered
        :loading="tableLoading"
        :pagination="false"
        :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
    >
      <template #bodyCell="{ text, record, column }">

        <!-- 有图片预览时 注释解开并把下面的'picture'修改成自己的图片字段名即可 -->
        <template v-if="column.dataIndex === 'currencyType'">
          {{ text && text.length > 0 ? text.map((e) => e.valueName).join(',') : '暂无' }}
        </template>

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

    <ReceivablesForm  ref="formRef" @reloadList="queryData"/>

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
        <a-button @click="onImportReceivables">
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
import { receivablesApi } from '/@/api/vigorous/receivables-api';
import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
import { smartSentry } from '/@/lib/smart-sentry';
import TableOperator from '/@/components/support/table-operator/index.vue';
import ReceivablesForm from './receivables-form.vue';
import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
import { defaultTimeRanges } from '/@/lib/default-time-ranges';
//import FilePreview from '/@/components/support/file-preview/index.vue'; // 图片预览组件
import DictSelect from '/@/components/support/dict-select/index.vue';
import {receivablesApi as excelApi} from "/@/api/vigorous/excel-api.js";


// ---------------------------- 表格列 ----------------------------

const columns = ref([
  {
    title: '应收表-单据编号',
    dataIndex: 'billNo',
    ellipsis: true,
  },
  {
    title: '应收日期',
    dataIndex: 'receivablesDate',
    ellipsis: true,
  },
  {
    title: '业务员编号',
    dataIndex: 'salespersonId',
    ellipsis: true,
  },
  {
    title: '客户编号',
    dataIndex: 'customerId',
    ellipsis: true,
  },
  {
    title: '源单编号',
    dataIndex: 'originBillNo',
    ellipsis: true,
  },
  {
    title: '税收合计',
    dataIndex: 'amount',
    ellipsis: true,
  },
  {
    title: '币别',
    dataIndex: 'currencyType',
    ellipsis: true,
  },
  {
    title: '应收比例(%)',
    dataIndex: 'rate',
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
  customerName: undefined, //客户名称
  salespersonName: undefined, //销售员名称
  currencyType: undefined, //币别
  receivablesDate: [], //收款日期
  receivablesDateBegin: undefined, //收款日期 开始
  receivablesDateEnd: undefined, //收款日期 结束
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
    let queryResult = await receivablesApi.queryPage(queryForm);
    tableData.value = queryResult.data.list;
    total.value = queryResult.data.total;
  } catch (e) {
    smartSentry.captureError(e);
  } finally {
    tableLoading.value = false;
  }
}

function onChangeReceivablesDate(dates, dateStrings){
  queryForm.receivablesDateBegin = dateStrings[0];
  queryForm.receivablesDateEnd = dateStrings[1];
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
    await receivablesApi.delete(data.receivablesId);
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
    await receivablesApi.batchDelete(selectedRowKeyList.value);
    message.success('删除成功');
    await queryData();
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
const importMode = ref(1)
// 导入文件
async function onImportReceivables() {
  const formData = new FormData();
  fileList.value.forEach((file) => {
    formData.append('file', file.originFileObj);
    formData.append('mode', importMode.value);
  });

  SmartLoading.show();
  try {
    let res = await receivablesApi.importReceivables(formData);
    console.log(res)
    failed_import_data.value = res.data;
    message.success(res.msg);
  } catch (e) {
    smartSentry.captureError(e);
  } finally {
    SmartLoading.hide();
  }
}

// 导出excel文件
async function onExportReceivables() {
  await receivablesApi.exportReceivables();
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
