package net.lab1024.sa.admin.module.vigorous.receivables.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.vigorous.receivables.dao.ReceivablesDetailsDao;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.entity.ReceivablesDetailsEntity;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesDetailsAddForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesDetailsQueryForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesDetailsUpdateForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.vo.ReceivablesDetailsVO;
import net.lab1024.sa.admin.util.BatchUtils;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 应收明细表 Service
 *
 * @Author yxz
 * @Date 2025-10-23 14:43:51
 * @Copyright (c)2025 yxz
 */

@Service
public class ReceivablesDetailsService {

    @Resource
    private ReceivablesDetailsDao receivablesDetailsDao;

    @Autowired
    private BatchUtils batchUtils;
    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<ReceivablesDetailsVO> queryPage(ReceivablesDetailsQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ReceivablesDetailsVO> list = receivablesDetailsDao.queryPage(page, queryForm);
        PageResult<ReceivablesDetailsVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ReceivablesDetailsAddForm addForm) {
        ReceivablesDetailsEntity receivablesDetailsEntity = SmartBeanUtil.copy(addForm, ReceivablesDetailsEntity.class);
        receivablesDetailsDao.insert(receivablesDetailsEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(ReceivablesDetailsUpdateForm updateForm) {
        ReceivablesDetailsEntity receivablesDetailsEntity = SmartBeanUtil.copy(updateForm, ReceivablesDetailsEntity.class);
        receivablesDetailsDao.updateById(receivablesDetailsEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     *
     * @param idList
     * @return
     */
    public ResponseDTO<String> batchDelete(List<Integer> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        receivablesDetailsDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Integer id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        receivablesDetailsDao.deleteById(id);
        return ResponseDTO.ok();
    }

    /**
     * 导出
     * 需要修改
     */
    public List<ReceivablesDetailsVO> exportReceivablesDetails(ReceivablesDetailsQueryForm queryForm) {
        //List<ReceivablesDetailsVO> entityList = receivablesDetailsDao.selectList(null);
        List<ReceivablesDetailsVO> entityList = receivablesDetailsDao.queryPage(null,queryForm);
//        return entityList.stream()
//                .map(e ->
//                        ReceivablesDetailsVO.builder()
//                                .build()
//                )
//                .collect(Collectors.toList());
        return null;

    }


}
