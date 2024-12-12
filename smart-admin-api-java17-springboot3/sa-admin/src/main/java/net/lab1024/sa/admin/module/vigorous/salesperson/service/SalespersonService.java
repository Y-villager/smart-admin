package net.lab1024.sa.admin.module.vigorous.salesperson.service;

import java.util.List;
import net.lab1024.sa.admin.module.vigorous.salesperson.dao.SalespersonDao;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.entity.SalespersonEntity;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonAddForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonQueryForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonUpdateForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.vo.SalespersonVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 业务员 Service
 *
 * @Author yxz
 * @Date 2024-12-12 14:42:49
 * @Copyright (c)2024 yxz
 */

@Service
public class SalespersonService {

    @Resource
    private SalespersonDao salespersonDao;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<SalespersonVO> queryPage(SalespersonQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<SalespersonVO> list = salespersonDao.queryPage(page, queryForm);
        PageResult<SalespersonVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(SalespersonAddForm addForm) {
        SalespersonEntity salespersonEntity = SmartBeanUtil.copy(addForm, SalespersonEntity.class);
        salespersonDao.insert(salespersonEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(SalespersonUpdateForm updateForm) {
        SalespersonEntity salespersonEntity = SmartBeanUtil.copy(updateForm, SalespersonEntity.class);
        salespersonDao.updateById(salespersonEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     *
     * @param idList
     * @return
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        salespersonDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        salespersonDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }
}
