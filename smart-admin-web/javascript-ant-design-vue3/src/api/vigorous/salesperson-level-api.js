/**
 * 业务员级别 api 封装
 *
 * @Author:    yxz
 * @Date:      2024-12-14 16:07:14
 * @Copyright  (c)2024 yxz
 */
import {postRequest, getRequest, getDownload} from '/src/lib/axios';

export const salespersonLevelApi = {

  /**
   * 分页查询  @author  yxz
   */
  queryPage : (param) => {
    return postRequest('/salespersonLevel/queryPage', param);
  },

  /**
   * 增加  @author  yxz
   */
  add: (param) => {
      return postRequest('/salespersonLevel/add', param);
  },

  /**
   * 修改  @author  yxz
   */
  update: (param) => {
      return postRequest('/salespersonLevel/update', param);
  },


  /**
   * 删除  @author  yxz
   */
  delete: (id) => {
      return getRequest(`/salespersonLevel/delete/${id}`);
  },

  /**
   * 批量删除  @author  yxz
   */
  batchDelete: (idList) => {
      return postRequest('/salespersonLevel/batchDelete', idList);
  },

    // 导入
    importSalespersonLevel : (file) =>{
        return postRequest('/salespersonLevel/import',file);
    },

    // 导出
    exportSalespersonLevel : () =>{
        return getDownload('/salespersonLevel/export');
    },

    queryList:() =>{
      return postRequest('/salespersonLevel/getAll');
    },

    updateLevel(data) {
        return postRequest('/salesperson/updateLevel', data)
    }
};
