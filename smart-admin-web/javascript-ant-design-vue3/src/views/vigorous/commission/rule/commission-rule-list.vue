<!--
  * 提成规则
  *
  * @Author:    yxz
  * @Date:      2024-12-23 16:14:00
  * @Copyright  (c)2024 yxz
-->
<template>
    <!---------- 查询表单form begin ----------->
    <a-form class="smart-query-form">
        <a-row class="smart-query-form-row">
          <a-form-item label="转交状态" class="smart-query-form-item">
            <SmartEnumSelect width="200px" v-model:value="queryForm.transferStatus" enumName="IS_TRANSFERRED_ENUM" placeholder="转交状态（0自主开发，非0转交）"/>
          </a-form-item>
            <a-form-item label="客户分组" class="smart-query-form-item">
                <SmartEnumSelect width="200px" v-model:value="queryForm.customerGroup" enumName="CUSTOMER_GROUP_ENUM" placeholder="客户分组"/>
            </a-form-item>
            <a-form-item label="提成类型" class="smart-query-form-item">
                <SmartEnumSelect width="200px" v-model:value="queryForm.commissionType" enumName="COMMISSION_TYPE_ENUM" placeholder="提成类型（1业务 2管理）"/>
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
<!--              <a-button @click="showImportModal" type="primary" v-privilege="'commissionRule:importCommissionRule'">-->
<!--                <template #icon>-->
<!--                  <ImportOutlined />-->
<!--                </template>-->
<!--                导入-->
<!--              </a-button>-->

              <a-button @click="onExportCommissionRule" type="primary" v-privilege="'commissionRule:exportCommissionRule'">
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
            rowKey="ruleId"
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
              <template v-if="column.dataIndex === 'transferStatus'">
                <span>{{$smartEnumPlugin.getDescByValue('IS_TRANSFERRED_ENUM', text)}}</span>
              </template>
              <template v-if="column.dataIndex === 'customerGroup'">
                <span>{{$smartEnumPlugin.getDescByValue('CUSTOMER_GROUP_ENUM', text)}}</span>
              </template>
              <template v-if="column.dataIndex === 'commissionType'">
                <span>{{$smartEnumPlugin.getDescByValue('COMMISSION_TYPE_ENUM', text)}}</span>
              </template>
              <template v-if="column.dataIndex === 'useDynamicFormula'">
                <span>{{$smartEnumPlugin.getDescByValue('SYSTEM_YES_NO', text)}}</span>
              </template>

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

        <CommissionRuleForm  ref="formRef" @reloadList="queryData"/>

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
          <a-button @click="onImportCommissionRule">
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
    import { commissionRuleApi } from '/@/api/vigorous/commission-rule-api';
    import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
    import { smartSentry } from '/@/lib/smart-sentry';
    import TableOperator from '/@/components/support/table-operator/index.vue';
    import {excelApi} from "/@/api/vigorous/excel-api.js";

    import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
    import CommissionRuleForm from './commission-rule-form.vue';
    import {IS_TRANSFERRED_ENUM} from "/@/constants/vigorous/customer-const.js";
    //import FilePreview from '/@/components/support/file-preview/index.vue'; // 图片预览组件

    // ---------------------------- 表格列 ----------------------------

    const columns = ref([
        {
            title: '转交状态',
            dataIndex: 'transferStatus',
            ellipsis: true,
        },
        {
            title: '客户分组',
            dataIndex: 'customerGroup',
            ellipsis: true,
        },
        {
            title: '提成类型',
            dataIndex: 'commissionType',
            ellipsis: true,
        },
        {
            title: '是否计算公式',
            dataIndex: 'useDynamicFormula',
            ellipsis: true,
        },
        {
            title: '提成系数',
            dataIndex: 'commissionRate',
            ellipsis: true,
        },
        {
            title: '计算公式id',
            dataIndex: 'formulaId',
            ellipsis: true,
        },
        {
            title: '备注',
            dataIndex: 'remark',
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
        customerGroup: undefined, //客户分组
        transferStatus: undefined, //转交状态（0自主开发，非0转交）
        commissionType: undefined, //提成类型（1业务 2管理）
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
            let queryResult = await commissionRuleApi.queryPage(queryForm);
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
            await commissionRuleApi.delete(data.ruleId);
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
            await commissionRuleApi.batchDelete(selectedRowKeyList.value);
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
async function onExportCommissionRule() {
  await commissionRuleApi.exportCommissionRule();
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
async function onImportCommissionRule() {
  const formData = new FormData();
  fileList.value.forEach((file) => {
    formData.append('file', file.originFileObj);
    formData.append('mode', importMode.value);
  });

  SmartLoading.show();
  try {
    let res = await commissionRuleApi.importCommissionRule(formData);
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
