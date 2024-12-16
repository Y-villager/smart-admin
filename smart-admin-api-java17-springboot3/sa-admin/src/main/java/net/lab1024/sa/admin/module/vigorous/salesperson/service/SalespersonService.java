package net.lab1024.sa.admin.module.vigorous.salesperson.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import net.lab1024.sa.admin.module.business.goods.domain.entity.GoodsEntity;
import net.lab1024.sa.admin.module.system.department.service.DepartmentService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

import static cn.dev33.satoken.SaManager.log;

/**
 * 业务员 Service
 *
 * @Author yxz
 * @Date 2024-12-16 10:56:45
 * @Copyright (c)2024 yxz
 */

@Service
public class SalespersonService {

    @Resource
    private SalespersonDao salespersonDao;
    @Autowired
    private DepartmentService departmentService;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<SalespersonVO> queryPage(SalespersonQueryForm queryForm) {
        queryForm.setDeletedFlag(false);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<SalespersonVO> list = salespersonDao.queryPage(page, queryForm);

        // 查询部门、业务员级别名称
        list.forEach(e -> {
            e.setDepartmentName(departmentService.queryDepartmentName(e.getDepartmentId()));
        });
        return SmartPageUtil.convert2PageResult(page, list);
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
     * 商品导入
     *
     * @param file 上传文件
     * @return 结果
     */
    public ResponseDTO<String> importSalesperson(MultipartFile file) {
        List<SalespersonImportForm> dataList;
        List<SalespersonImportForm> failedDataList = new ArrayList<>();
        List<SalespersonEntity> entityList = new ArrayList<>();

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

        // 查询所有已存在的业务员编码，避免重复插入
        Set<String> existingCodes = salespersonDao.getAllSalespersonCodes();

        // 将 SalespersonImportForm 转换为 SalespersonEntity，同时记录失败的数据
        for (SalespersonImportForm form : dataList) {
            // 检查业务员编码是否重复
            if (existingCodes.contains(form.getSalespersonCode())) {
                // 如果重复，将该条记录标记为失败
                form.setErrorMessage("业务员编码重复");
                failedDataList.add(form);
                continue;
            }

            // 将有效的记录转换为实体
            SalespersonEntity entity = convertToEntity(form);
            if (entity != null) {
                entityList.add(entity);
            } else {
                // 如果转换失败，记录失败信息
                form.setErrorMessage("找不到部门");
                failedDataList.add(form);
            }
        }
        // 批量插入有效数据
        List<BatchResult> insert = salespersonDao.insert(entityList);

        // 如果有失败的数据，导出失败记录到 Excel
        if (!failedDataList.isEmpty()) {
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            EasyExcel.write(outputStream, SalespersonImportForm.class)
//                    .sheet("导入失败记录")
//                    .doWrite(failedDataList);
//
//            // 假设保存导出的失败 Excel 文件的路径
//            String failedFilePath = saveFailedFile(outputStream);

            return ResponseDTO.okMsg("成功导入" + insert.size() + "条，失败记录已保存至：" );
        }

        return ResponseDTO.okMsg("成功导入" + insert.size() + "条数据");
    }

    // 将 SalespersonImportForm 转换为 SalespersonEntity
    private SalespersonEntity convertToEntity(SalespersonImportForm form) {
        SalespersonEntity entity = new SalespersonEntity();

        entity.setSalespersonCode(form.getSalespersonCode());
        entity.setSalespersonName(form.getSalespersonName());

        // 查找部门 ID
        Long departmentId = departmentService.getDepartmentIdByName(form.getDepartmentName());
        if (departmentId == null) {
            log.warn("找不到部门: {}", form.getDepartmentName());
//            form.setErrorMessage("找不到部门");
//            failedDataList.add(form);
            return null;
        }
        entity.setDepartmentId(departmentId);

        return entity;
    }

    /**
     * 商品导出
     */
    public List<SalespersonExcelVO> getAllSalesperson() {
        List<SalespersonEntity> goodsEntityList = salespersonDao.selectList(null);
        return goodsEntityList.stream()
                .map(e ->
                        SalespersonExcelVO.builder()
                                .salespersonCode(e.getSalespersonCode())
                                .salespersonName(e.getSalespersonName())
//                                .salespersonLevel()
                                .department(departmentService.queryDepartmentName(e.getDepartmentId()))
                                .build()
                )
                .collect(Collectors.toList());

    }


}
