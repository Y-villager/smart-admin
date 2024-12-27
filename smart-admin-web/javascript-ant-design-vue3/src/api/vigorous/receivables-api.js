/**
 * 应收单 api 封装
 *
 * @Author:    yxz
 * @Date:      2024-12-12 14:46:31
 * @Copyright  yxz
 */
import { postRequest, getRequest, getDownload } from '/src/lib/axios';

export const receivablesApi = {

  /**
   * 分页查询  @author  yxz
   */
  queryPage : (param) => {
    return postRequest('/receivables/queryPage', param);
  },

  /**
   * 增加  @author  yxz
   */
  add: (param) => {
      return postRequest('/receivables/add', param);
  },

  /**
   * 修改  @author  yxz
   */
  update: (param) => {
      return postRequest('/receivables/update', param);
  },


  /**
   * 删除  @author  yxz
   */
  delete: (id) => {
      return getRequest(`/receivables/delete/${id}`);
  },

  /**
   * 批量删除  @author  yxz
   */
  batchDelete: (idList) => {
      return postRequest('/receivables/batchDelete', idList);
  },

    // 导入
    importReceivables : (file) =>{
        return postRequest('/receivables/import',file);
    },

    // 导出
    exportReceivables : () =>{
        return getDownload('/receivables/export');
    }

};
