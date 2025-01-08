/**
 * 业务员级别变动记录 api 封装
 *
 * @Author:    yxz
 * @Date:      2025-01-07 08:58:41
 * @Copyright  (c)2024 yxz
 */
import { postRequest, getRequest, getDownload } from '/@/lib/axios';

export const salespersonLevelRecordApi = {

  /**
   * 分页查询  @author  yxz
   */
  queryPage : (param) => {
    return postRequest('/salespersonLevelRecord/queryPage', param);
  },

  /**
   * 增加  @author  yxz
   */
  add: (param) => {
      return postRequest('/salespersonLevelRecord/add', param);
  },

  /**
   * 修改  @author  yxz
   */
  update: (param) => {
      return postRequest('/salespersonLevelRecord/update', param);
  },



    // 导入
    importSalespersonLevelRecord : (file) =>{
        return postRequest('/salespersonLevelRecord/import',file);
    },

    // 导出
    exportSalespersonLevelRecord : () =>{
        return getDownload('/salespersonLevelRecord/export');
    }

};
