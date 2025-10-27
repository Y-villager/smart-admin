/**
 * 发货通知单 api 封装
 *
 * @Author:    yxz
 * @Date:      2025-10-23 14:11:35
 * @Copyright  (c)2024 yxz
 */
import { postRequest, getRequest, postDownload } from '/src/lib/axios';

export const fSalesApi = {

  /**
   * 分页查询  @author  yxz
   */
  queryPage : (param) => {
    return postRequest('/fSales/queryPage', param);
  },

  /**
   * 增加  @author  yxz
   */
  add: (param) => {
      return postRequest('/fSales/add', param);
  },

  /**
   * 修改  @author  yxz
   */
  update: (param) => {
      return postRequest('/fSales/update', param);
  },


  /**
   * 删除  @author  yxz
   */
  delete: (id) => {
      return getRequest(`/fSales/delete/${id}`);
  },

  /**
   * 批量删除  @author  yxz
   */
  batchDelete: (idList) => {
      return postRequest('/fSales/batchDelete', idList);
  },

    // 导入
    importFSales : (file) =>{
        return postRequest('/fSales/import',file);
    },

    // 导出
    exportFSales : (data) =>{
        return postDownload('/fSales/export', data);
    }

};
