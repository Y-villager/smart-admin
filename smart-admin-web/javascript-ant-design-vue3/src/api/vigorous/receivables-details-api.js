/**
 * 应收明细表 api 封装
 *
 * @Author:    yxz
 * @Date:      2025-10-23 14:43:51
 * @Copyright  (c)2025 yxz
 */
import { postRequest, getRequest, postDownload } from '/src/lib/axios';

export const receivablesDetailsApi = {

  /**
   * 分页查询  @author  yxz
   */
  queryPage : (param) => {
    return postRequest('/receivablesDetails/queryPage', param);
  },

  /**
   * 增加  @author  yxz
   */
  add: (param) => {
      return postRequest('/receivablesDetails/add', param);
  },

  /**
   * 修改  @author  yxz
   */
  update: (param) => {
      return postRequest('/receivablesDetails/update', param);
  },


  /**
   * 删除  @author  yxz
   */
  delete: (id) => {
      return getRequest(`/receivablesDetails/delete/${id}`);
  },

  /**
   * 批量删除  @author  yxz
   */
  batchDelete: (idList) => {
      return postRequest('/receivablesDetails/batchDelete', idList);
  },

    // 导入
    importReceivablesDetails : (file) =>{
        return postRequest('/receivablesDetails/import',file);
    },

    // 导出
    exportReceivablesDetails : (data) =>{
        return postDownload('/receivablesDetails/export', data);
    }

};
