/**
 * 顾客 api 封装
 *
 * @Author:    yxz
 * @Date:      2024-12-12 14:51:07
 * @Copyright  (c)2024 yxz
 */
import {postRequest, getRequest, getDownload} from '/@/lib/axios';

export const customerApi = {

  /**
   * 分页查询  @author  yxz
   */
  queryPage : (param) => {
    return postRequest('/customer/queryPage', param);
  },

  /**
   * 增加  @author  yxz
   */
  add: (param) => {
      return postRequest('/customer/add', param);
  },

  /**
   * 修改  @author  yxz
   */
  update: (param) => {
      return postRequest('/customer/update', param);
  },


  /**
   * 删除  @author  yxz
   */
  delete: (id) => {
      return getRequest(`/customer/delete/${id}`);
  },

  /**
   * 批量删除  @author  yxz
   */
  batchDelete: (idList) => {
      return postRequest('/customer/batchDelete', idList);
  },

    // 导入
    importCustomer : (file) =>{
        return postRequest('/customer/import',file);
    },

    // 导出
    exportCustomer : () =>{
        return getDownload('/customer/export');
    }

};
