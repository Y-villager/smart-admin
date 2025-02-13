/**
 * 业务提成记录 api 封装
 *
 * @Author:    yxz
 * @Date:      2025-01-12 15:25:35
 * @Copyright  (c)2024 yxz
 */
import {postRequest, getRequest, getDownload, postDownload} from '/src/lib/axios';

export const commissionRecordApi = {

  /**
   * 分页查询  @author  yxz
   */
  queryPage : (param) => {
    return postRequest('/commissionRecord/queryPage', param);
  },

  /**
   * 增加  @author  yxz
   */
  add: (param) => {
      return postRequest('/commissionRecord/add', param);
  },

  /**
   * 修改  @author  yxz
   */
  update: (param) => {
      return postRequest('/commissionRecord/update', param);
  },


  /**
   * 删除  @author  yxz
   */
  delete: (id) => {
      return getRequest(`/commissionRecord/delete/${id}`);
  },

  /**
   * 批量删除  @author  yxz
   */
  batchDelete: (idList) => {
      return postRequest('/commissionRecord/batchDelete', idList);
  },

    // 导入
    importCommissionRecord : (file) =>{
        return postRequest('/commissionRecord/import',file);
    },

    // 导出
    exportCommissionRecord : (form) =>{
        return postDownload('/commissionRecord/export', form);
    }

};
