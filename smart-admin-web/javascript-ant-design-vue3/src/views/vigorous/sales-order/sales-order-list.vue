<!--
  * 销售订单表
  *
  * @Author:    yxz
  * @Date:      2025-10-23 14:10:32
  * @Copyright  (c)2024 yxz
-->
<template>
    <!---------- 查询表单form begin ----------->
    <a-form class="smart-query-form">
        <a-row class="smart-query-form-row">
          <a-form-item label="单据编号" class="smart-query-form-item">
            <a-input style="width: 200px" v-model:value="queryForm.billNo" placeholder="业务员" />
          </a-form-item>
            <a-form-item label="单据日期" class="smart-query-form-item">
              <a-range-picker v-model:value="queryForm.orderDate" :presets="defaultTimeRanges" style="width: 220px" @change="onChangeOrderDate" />
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
            <a-form-item class="smart-query-form-item">
                <a-button type="primary" @click="onSearch" v-privilege="'salesOrder:query'">
                    <template #icon>
                        <SearchOutlined />
                    </template>
                    查询
                </a-button>
                <a-button @click="resetQuery" class="smart-margin-left10" v-privilege="'salesOrder:query'">
                    <template #icon>
                        <ReloadOutlined />
                    </template>
                    重置
                </a-button>
              <a-button class="smart-margin-left20" @click="moreCreateCondition = !moreCreateCondition">
                <template #icon>
                  <MoreOutlined />
                </template>
                {{ moreCreateCondition ? '收起' : '展开' }}
              </a-button>
            </a-form-item>
        </a-row>

      <!--  多次生成情况   -->
      <a-row class="smart-query-form-row" v-show="moreCreateCondition">
        <a-form-item label="生成提成方式" class="smart-query-form-item">
          <a-button  @click="onExportSelectedCommission"
                     type="primary"
                     :disabled="selectedRowKeyList.length === 0"
                     v-privilege="'salesOutbound:createCommission'"
                     :loading="initDisabled">
            <template #icon>
              <ExportOutlined />
            </template>
            生成选中部分
          </a-button>
          <a-button class="smart-margin-left20" @click="onExportAllCommission" danger v-privilege="'salesOutbound:createCommission'" :loading="initDisabled">
            <template #icon>
              <ExportOutlined />
            </template>
            生成全部
          </a-button>
          <a-button  class="smart-margin-left20" @click="onForceCreateCommission"
                     :disabled="selectedRowKeyList.length === 0"
                     v-privilege="'salesOutbound:createCommission'"
                     :loading="initDisabled">
            <template #icon>
              <ExportOutlined />
            </template>
            强制生成
          </a-button>
          <a-button  @click="downloadFailedCommission" type="link" v-show="failedCommissionPath!==undefined && failedCommissionPath!==null">
            下载生成失败数据
          </a-button>
        </a-form-item>
      </a-row>
      <a-row class="smart-query-form-row" v-show="moreCreateCondition">
        <a-form-item label="排除" class="smart-query-form-item">
          <a-checkbox-group :default-value="selectedValues"  @change="handleSelectionChange">
            <a-checkbox value="customerName" >个人客户</a-checkbox>
            <a-checkbox value="deletedSalesman" >已删除业务员</a-checkbox>
            <a-checkbox value="disabledSalesman">已禁用业务员</a-checkbox>
          </a-checkbox-group>
        </a-form-item>
      </a-row>
    </a-form>
    <!---------- 查询表单form end ----------->

    <a-card size="small" :bordered="false" :hoverable="true">
        <!---------- 表格操作行 begin ----------->
        <a-row class="smart-table-btn-block">
            <div class="smart-table-operate-block">
                <a-button @click="showForm" type="primary" v-privilege="'salesOrder:add'">
                    <template #icon>
                        <PlusOutlined />
                    </template>
                    新建
                </a-button>
                <a-button @click="confirmBatchDelete" type="primary" danger :disabled="selectedRowKeyList.length === 0"
                          v-privilege="'salesOrder:batchDelete'">
                    <template #icon>
                        <DeleteOutlined />
                    </template>
                    批量删除
                </a-button>
              <a-button @click="showImportModal" type="primary" v-privilege="'salesOrder:import'">
                <template #icon>
                  <ImportOutlined />
                </template>
                导入
              </a-button>

              <a-button @click="onExportSalesOrder" type="primary" v-privilege="'salesOrder:export'" :loading="exportLoading">
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
            rowKey="salesOrderId"
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

                <template v-if="column.dataIndex === 'action'">
                    <div class="smart-table-operate">
                        <a-button @click="showForm(record)" type="link" v-privilege="'salesOrder:update'">编辑</a-button>
                        <a-button @click="onDelete(record)" danger type="link" v-privilege="'salesOrder:delete'">删除</a-button>
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

        <SalesOrderForm  ref="formRef" @reloadList="queryData"/>

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
          <a-button @click="onImportSalesOrder">
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
    import { salesOrderApi } from '/@/api/vigorous/sales-order-api';
    import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
    import { smartSentry } from '/@/lib/smart-sentry';
    import TableOperator from '/@/components/support/table-operator/index.vue';
    import {excelApi} from "/@/api/vigorous/excel-api.js";

    import SalesOrderForm from './sales-order-form.vue';
    import { defaultTimeRanges } from '/@/lib/default-time-ranges';
    import {salesOutboundApi} from "/@/api/vigorous/sales-outbound-api.js";
    //import FilePreview from '/@/components/support/file-preview/index.vue'; // 图片预览组件

    // ---------------------------- 表格列 ----------------------------

    const columns = ref([
        {
            title: '单据编号-销售订单',
            dataIndex: 'billNo',
            ellipsis: true,
        },
        {
            title: '单据日期',
            dataIndex: 'orderDate',
            ellipsis: true,
        },
        {
            title: '客户编码',
            dataIndex: 'customerCode',
            ellipsis: true,
        },
        {
            title: '销售员编码',
            dataIndex: 'salespersonCode',
            ellipsis: true,
        },
        {
            title: '单据类型（0配件 1整车',
            dataIndex: 'orderType',
            ellipsis: true,
        },
        {
            title: '价税合计',
            dataIndex: 'amount',
            ellipsis: true,
        },
        {
            title: '汇率',
            dataIndex: 'exchangeRate',
            ellipsis: true,
        },
        {
            title: '价税合计（本位币）',
            dataIndex: 'fallAmount',
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
        billNo: undefined,
        orderDate: undefined,
        orderDateBegin: undefined, //单据日期-开始
        orderDateEnd: undefined, //单据日期-结束
        customerName: undefined, //客户
        salespersonName: undefined, //业务员
        departmentName: undefined, //部门
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
    const excludeFormState = {
      customerName: '个人',
      deletedSalesmanFlag: 1,
      disabledSalesmanFlag: 1
    };
    const excludeForm = reactive({ ...excludeFormState });

    // 重置查询条件
    function resetQuery() {
        let pageSize = queryForm.pageSize;
        Object.assign(queryForm, queryFormState);
        Object.assign(excludeForm, excludeFormState);
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
            let queryResult = await salesOrderApi.queryPage(queryForm);
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
            await salesOrderApi.delete(data.salesOrderId);
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
            await salesOrderApi.batchDelete(selectedRowKeyList.value);
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
  const exportLoading = ref(false);
  async function onExportSalesOrder() {
    exportLoading.value = true
    await salesOrderApi.exportSalesOrder(queryForm);
    exportLoading.value = false
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
  async function onImportSalesOrder() {
    const formData = new FormData();
    fileList.value.forEach((file) => {
      formData.append('file', file.originFileObj);
      formData.append('mode', importMode.value);
    });

    SmartLoading.show();
    try {
      let res = await salesOrderApi.importSalesOrder(formData);
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

// 日期范围修改
    // ---------------------出库日期选择 事件--------------------------
    function onChangeOrderDate(dates, dateStrings) {
      queryForm.orderDateBegin = dateStrings[0];
      queryForm.orderDateEnd = dateStrings[1];
    }

    // 生成提成情况
    const moreCreateCondition = ref(true);
    const selectedValues = ref(['customerName', 'deletedSalesman', 'disabledSalesman']);

    // 生成提成失败路径
    const failedCommissionPath = ref();
    const initDisabled = ref(false);

    // 生成选中部分的提成
    async function onExportSelectedCommission(){
      try{
        failedCommissionPath.value = undefined
        console.log(selectedRowKeyList.value)
        let res = await salesOrderApi.createSelectedCommission(selectedRowKeyList.value);
        failedCommissionPath.value = res.data
        message.success(res.msg)
      }catch (error){
        console.log("错误：", error)
      }finally {
        await queryData()
      }
    }

    // 生成所有提成
    async function onForceCreateCommission() {
      initDisabled.value = true;
      try{
        failedCommissionPath.value = undefined
        console.log(selectedRowKeyList.value)
        let res = await salesOrderApi.forceCreateCommission(selectedRowKeyList.value);
        failedCommissionPath.value = res.data
        message.success(res.msg)
      }catch (error){
        console.log("错误：", error)
      }finally {
        initDisabled.value = false;
        await queryData()
      }
    }

    // 强制生成
    async function onExportAllCommission(){
      initDisabled.value = true;
      try {
        failedCommissionPath.value = undefined
        let res = await salesOrderApi.createAllCommission({
          queryForm: queryForm,
          excludeForm: excludeForm
        });
        failedCommissionPath.value = res.data
        initDisabled.value = false;
        message.success(res.msg)
      } catch (error) {
        console.log('错误:', error);
        initDisabled.value = false;
      }finally {
        await queryData()
      }
    }

    // 下载失败
    function downloadFailedCommission(){
      try {
        const path = failedCommissionPath.value;
        if (path !== undefined && path != null){
          console.log(path)
          excelApi.downloadFailedImportData(path);
        }
      } catch (e) {
        smartSentry.captureError(e);
      }
    }

</script>
