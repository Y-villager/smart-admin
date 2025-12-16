/**
 * 销售订单表 api 封装
 *
 * @Author:    yxz
 * @Date:      2025-10-23 14:10:32
 * @Copyright  (c)2024 yxz
 */
import { postRequest, getRequest, postDownload } from '/src/lib/axios';

export const salesOrderApi = {

  /**
   * 分页查询  @author  yxz
   */
  queryPage : (param) => {
    return postRequest('/salesOrder/queryPage', param);
  },

  /**
   * 增加  @author  yxz
   */
  add: (param) => {
      return postRequest('/salesOrder/add', param);
  },

  /**
   * 修改  @author  yxz
   */
  update: (param) => {
      return postRequest('/salesOrder/update', param);
  },


  /**
   * 删除  @author  yxz
   */
  delete: (id) => {
      return getRequest(`/salesOrder/delete/${id}`);
  },

  /**
   * 批量删除  @author  yxz
   */
  batchDelete: (idList) => {
      return postRequest('/salesOrder/batchDelete', idList);
  },

    // 导入
    importSalesOrder : (file) =>{
        return postRequest('/salesOrder/import',file);
    },

    // 导出
    exportSalesOrder : (data) =>{
        return postDownload('/salesOrder/export', data);
    },

    // 生成业绩提成
    createAllCommission : (param) =>{
        return postRequest('/salesOrder/createAllCommission', param);
    },

    // 生成选中的业绩提成
    createSelectedCommission : (idList) =>{
        return postRequest('/salesOrder/createSelectedCommission', idList);
    },

    forceCreateCommission(idList) {
        return postRequest('/salesOrder/forceCreateCommission', idList);

    }
};
