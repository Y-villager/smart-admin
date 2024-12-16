/**
 * 业务员 api 封装
 *
 * @Author:    yxz
 * @Date:      2024-12-12 14:42:49
 * @Copyright  (c)2024 yxz
 */
import {postRequest, getRequest, getDownload} from '/@/lib/axios';

export const salespersonApi = {

  /**
   * 分页查询  @author  yxz
   */
  queryPage : (param) => {
    return postRequest('/salesperson/queryPage', param);
  },

  /**
   * 增加  @author  yxz
   */
  add: (param) => {
      return postRequest('/salesperson/add', param);
  },

  /**
   * 修改  @author  yxz
   */
  update: (param) => {
      return postRequest('/salesperson/update', param);
  },


  /**
   * 删除  @author  yxz
   */
  delete: (id) => {
      return getRequest(`/salesperson/delete/${id}`);
  },

  /**
   * 批量删除  @author  yxz
   */
  batchDelete: (idList) => {
      return postRequest('/salesperson/batchDelete', idList);
  },

    // 导入
    importSalesperson : (file) =>{
        return postRequest('/salesperson/import',file);
    },

    // 导出
    exportSalesperson : () =>{
        return getDownload('/salesperson/export');
    }




};
