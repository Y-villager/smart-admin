package net.lab1024.sa.admin.module.vigorous.salesperson.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import net.lab1024.sa.admin.module.business.goods.constant.GoodsStatusEnum;
import net.lab1024.sa.admin.module.business.goods.domain.entity.GoodsEntity;
import net.lab1024.sa.admin.module.business.goods.domain.form.GoodsImportForm;
import net.lab1024.sa.admin.module.business.goods.domain.vo.GoodsExcelVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.dao.SalespersonDao;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.entity.SalespersonEntity;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonAddForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonImportForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonQueryForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonUpdateForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.vo.SalespersonExcelVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.vo.SalespersonVO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartEnumUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

import static cn.dev33.satoken.SaManager.log;

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

    /**
     * 导入
     *
     * @param file 上传文件
     * @return 结果
     */
    public ResponseDTO<String> importSalesPerson(MultipartFile file) {
        List<SalespersonImportForm> dataList;
        try {
            dataList = EasyExcel.read(file.getInputStream()).head(SalespersonImportForm.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("数据格式存在问题，无法读取");
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseDTO.userErrorParam("数据为空");
        }
        List<SalespersonEntity> entityList = SmartBeanUtil.copyList(dataList, SalespersonEntity.class);

        List<BatchResult> insert = new ArrayList<>();
        try {
            insert = salespersonDao.insert(entityList);
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }

        return ResponseDTO.okMsg("成功导入" + insert.size() + "条，具体数据为：" + JSON.toJSONString(dataList));
    }

    /**
     * 导出
     */
    public List<SalespersonExcelVO> getAllSalesperson() {
        List<SalespersonEntity> salespersonEntityList = salespersonDao.selectList(null);
        return salespersonEntityList.stream()
                .map(e ->
                        SalespersonExcelVO.builder()
                                .position(e.getPosition())
                                .salespersonName(e.getSalespersonName())
                                .salespersonCode(e.getSalespersonCode())
                                .department(e.getDepartment())
                                .build()
                )
                .collect(Collectors.toList());

    }
}
