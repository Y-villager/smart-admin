<!--
  * 销售出库
  *
  * @Author:    yxz
  * @Date:      2024-12-12 14:48:19
  * @Copyright  (c)2024 yxz
-->
<template>
  <!---------- 查询表单form begin ----------->
  <a-form class="smart-query-form">
    <a-row class="smart-query-form-row">
      <a-form-item label="单据编号" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.billNo" placeholder="单据编号" />
      </a-form-item>
      <a-form-item label="客户名称" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.customerName" placeholder="客户名称" />
      </a-form-item>
      <a-form-item label="部门名称" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.departmentName" placeholder="部门名称" />
      </a-form-item>
      <a-form-item label="销售员" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.salespersonName" placeholder="销售员" />
      </a-form-item>
      <a-form-item label="出库日期" class="smart-query-form-item">
        <a-range-picker v-model:value="queryForm.salesBoundDate" :presets="defaultTimeRanges" style="width: 220px" @change="onChangeSalesBoundDate" />
      </a-form-item>
      <a-form-item label="是否存在首单" class="smart-query-form-item">
        <SmartEnumSelect enum-name="SYSTEM_YES_NO" placeholder="是否存在首单" v-model:value="queryForm.hasFirstOrder" width="160px" />
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
        <a-button @click="showImportModal" type="primary" v-privilege="'salesOutbound:import'">
          <template #icon>
            <ImportOutlined />
          </template>
          导入
        </a-button>

        <a-button @click="onExportSalesOutbound" type="primary" v-privilege="'salesOutbound:export'" :disabled="exportDisabled">
          <template #icon>
            <ExportOutlined />
          </template>
          导出
        </a-button>
        <a-button @click="onExportCommission" danger v-privilege="'salesOutbound:export'" :disabled="initDisabled">
          <template #icon>
            <ExportOutlined />
          </template>
          生成业绩提成
        </a-button>
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.VIGOROUS.SALES_OUTBOUND" :refresh="queryData" />
      </div>
    </a-row>
    <!---------- 表格操作行 end ----------->

    <!---------- 表格 begin ----------->
    <a-table
      size="small"
      :dataSource="tableData"
      :columns="columns"
      rowKey="salesBoundId"
      bordered
      :loading="tableLoading"
      :pagination="false"
      :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
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
        <!--              <template v-if="column.dataIndex==='receivables.currency'">-->
        <!--                {{ text !=null ? text.currency_type : '' }}-->
        <!--              </template>-->
        <!--              <template v-if="column.dataIndex==='receivables.rate'">-->
        <!--                {{ text !=null ? text.rate : '' }}-->
        <!--              </template>-->

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

    <SalesOutboundForm ref="formRef" @reloadList="queryData" />

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
        <a-button @click="onImportSalesOutbound">
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
      import { salesOutboundApi } from '/@/api/vigorous/sales-outbound-api';
      import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
      import { smartSentry } from '/@/lib/smart-sentry';
      import TableOperator from '/@/components/support/table-operator/index.vue';
      import SalesOutboundForm from './sales-outbound-form.vue';
      import {defaultTimeRanges} from "/@/lib/default-time-ranges.js";
      import {TABLE_ID_CONST} from "/@/constants/support/table-id-const.js";
      import DictSelect from "/@/components/support/dict-select/index.vue";
      import SmartEnumSelect from "/@/components/framework/smart-enum-select/index.vue";
      //import FilePreview from '/@/components/support/file-preview/index.vue'; // 图片预览组件

      // ---------------------------- 表格列 ----------------------------

      const columns = ref([
          {
              title: '单据编号',
              dataIndex: 'billNo',
          },
          {
              title: '出库日期',
              dataIndex: 'salesBoundDate',
          },
          {
              title: '客户名称',
              dataIndex: 'customerName',
              width: '200px',
              ellipsis: true,
          },
          {
              title: '销售员',
              dataIndex: 'salespersonName',
              ellipsis: true,
          },
          {
              title: '金额',
              dataIndex: 'amount',
              ellipsis: true,
          },
        {
          title: '首单日期',
          dataIndex: 'firstOrderDate',
          ellipsis: true,
        },
        {
          title: '创建时间',
          dataIndex: 'createTime',
          ellipsis: true,
        },

        {
          title: '更新时间',
          dataIndex: 'updateTime',
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
          billNo: undefined, //单据编号
          customerName: undefined, //客户编码
          salespersonName: undefined, //业务员
          salesBoundDateBegin: undefined, //出库日期 开始
          salesBoundDateEnd: undefined, //出库日期 结束
          salesBoundDate: undefined,
          billStatus: undefined,
          departmentName: "外贸部", // 部门名称
          hasFirstOrder: undefined, // 是否存在首单日期
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
      //
      const exportDisabled = ref(false);

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
              let queryResult = await salesOutboundApi.queryPage(queryForm);
              tableData.value = queryResult.data.list;
              total.value = queryResult.data.total;
          } catch (e) {
              smartSentry.captureError(e);
          } finally {
              tableLoading.value = false;
          }
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
              await salesOutboundApi.delete(data.salesBoundId);
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
              await salesOutboundApi.batchDelete(selectedRowKeyList.value);
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
  // 导入文件
  async function onImportSalesOutbound() {
    const formData = new FormData();
    fileList.value.forEach((file) => {
      formData.append('file', file.originFileObj);
    });

    SmartLoading.show();
    try {
      let res = await salesOutboundApi.importSalesOutbound(formData);
      mesage.success(res.msg);
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  // 导出excel文件
  async function onExportSalesOutbound() {
    exportDisabled.value = true
    await salesOutboundApi.exportSalesOutbound(queryForm);

    exportDisabled.value = false
  }

  const initDisabled = ref(false)
  async function onExportCommission(){
    initDisabled.value = true
    await salesOutboundApi.createCommission(queryForm);
    initDisabled.value = false
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

    // ---------------------出库日期选择 事件--------------------------
      function onChangeSalesBoundDate(dates, dateStrings){
        queryForm.salesBoundDateBegin = dateStrings[0]
        queryForm.salesBoundDateEnd = dateStrings[1]
      }
</script>
