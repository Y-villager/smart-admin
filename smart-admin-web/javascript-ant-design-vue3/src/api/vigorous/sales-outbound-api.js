/**
 * 销售出库 api 封装
 *
 * @Author:    yxz
 * @Date:      2024-12-12 14:48:19
 * @Copyright  (c)2024 yxz
 */
import { postRequest, getRequest } from '/src/lib/axios';

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
    importSalesOutbound : (file) =>{
        return postRequest('/salesOutbound/import',file);
    },

    // 导出
    exportSalesOutbound : () =>{
        return getDownload('/salesOutbound/export');
    }

};