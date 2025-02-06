/**
 * 提成规则 api 封装
 *
 * @Author:    yxz
 * @Date:      2024-12-23 16:14:00
 * @Copyright  (c)2024 yxz
 */
import {postRequest, getRequest, getDownload, postDownload} from '/src/lib/axios';

export const commissionRuleApi = {

  /**
   * 分页查询  @author  yxz
   */
  queryPage : (param) => {
    return postRequest('/commissionRule/queryPage', param);
  },

  /**
   * 增加  @author  yxz
   */
  add: (param) => {
      return postRequest('/commissionRule/add', param);
  },

  /**
   * 修改  @author  yxz
   */
  update: (param) => {
      return postRequest('/commissionRule/update', param);
  },


  /**
   * 删除  @author  yxz
   */
  delete: (id) => {
      return getRequest(`/commissionRule/delete/${id}`);
  },

  /**
   * 批量删除  @author  yxz
   */
  batchDelete: (idList) => {
      return postRequest('/commissionRule/batchDelete', idList);
  },

    // 导入
    importCommissionRule : (file) =>{
        return postRequest('/commissionRule/import',file);
    },

    // 导出
    exportCommissionRule : (data) =>{
        return postDownload('/commissionRule/export', data);
    }

};
