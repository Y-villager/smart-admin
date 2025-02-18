/**
 * 销售出库 api 封装
 *
 * @Author:    yxz
 * @Date:      2024-12-12 14:48:19
 * @Copyright  (c)2024 yxz
 */
import {postRequest, getRequest, getDownload, postDownload} from '/src/lib/axios';

export const salesOutboundApi = {

  /**
   * 分页查询  @author  yxz
   */
  queryPage : (param) => {
    return postRequest('/salesOutbound/queryPage', param);
  },

  /**
   * 增加  @author  yxz
   */
  add: (param) => {
      return postRequest('/salesOutbound/add', param);
  },

  /**
   * 修改  @author  yxz
   */
  update: (param) => {
      return postRequest('/salesOutbound/update', param);
  },


  /**
   * 删除  @author  yxz
   */
  delete: (id) => {
      return getRequest(`/salesOutbound/delete/${id}`);
  },

  /**
   * 批量删除  @author  yxz
   */
  batchDelete: (idList) => {
      return postRequest('/salesOutbound/batchDelete', idList);
  },

    // 导入
    importSalesOutbound : (form) =>{
        return postRequest('/salesOutbound/import', form);
    },

    // 导出
    exportSalesOutbound : (param) =>{
        return postDownload('/salesOutbound/export', param);
    },

    // 生成业绩提成
    createAllCommission : (param) =>{
        return postRequest('/salesOutbound/createAllCommission', param);
    },

    // 生成选中的业绩提成
    createSelectedCommission : (idList) =>{
        return postRequest('/salesOutbound/createSelectedCommission', idList);
    },

    batchUpdate(idList) {
        return postRequest('/salesOutbound/batchUpdateCommissionFlag', idList)
    }
};
