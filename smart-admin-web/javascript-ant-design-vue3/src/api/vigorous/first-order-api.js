/**
 * 客户首单信息 api 封装
 *
 * @Author:    yxz
 * @Date:      2024-12-12 14:50:26
 * @Copyright  (c)2024 yxz
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const firstOrderApi = {

  /**
   * 分页查询  @author  yxz
   */
  queryPage : (param) => {
    return postRequest('/firstOrder/queryPage', param);
  },

  /**
   * 增加  @author  yxz
   */
  add: (param) => {
      return postRequest('/firstOrder/add', param);
  },

  /**
   * 修改  @author  yxz
   */
  update: (param) => {
      return postRequest('/firstOrder/update', param);
  },


  /**
   * 删除  @author  yxz
   */
  delete: (id) => {
      return getRequest(`/firstOrder/delete/${id}`);
  },

  /**
   * 批量删除  @author  yxz
   */
  batchDelete: (idList) => {
      return postRequest('/firstOrder/batchDelete', idList);
  },

    // 导入
    importFirstOrder : (file) =>{
        return postRequest('/firstOrder/import',file);
    },

    // 导出
    exportFirstOrder : () =>{
        return getDownload('/firstOrder/export');
    }

};
