package net.lab1024.sa.admin.module.vigorous.salespersonlevel.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.dao.SalespersonLevelDao;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.entity.SalespersonLevelEntity;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelAddForm;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelQueryForm;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelUpdateForm;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.vo.SalespersonLevelExcelVO;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.vo.SalespersonLevelVO;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.dev33.satoken.SaManager.log;

/**
 * 业务员级别 Service
 *
 * @Author yxz
 * @Date 2024-12-14 16:07:14
 * @Copyright (c)2024 yxz
 */

@Service
public class SalespersonLevelService {

    @Resource
    private SalespersonLevelDao salespersonLevelDao;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<SalespersonLevelVO> queryPage(SalespersonLevelQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<SalespersonLevelVO> list = salespersonLevelDao.queryPage(page, queryForm);
        PageResult<SalespersonLevelVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(SalespersonLevelAddForm addForm) {
        SalespersonLevelEntity salespersonLevelEntity = SmartBeanUtil.copy(addForm, SalespersonLevelEntity.class);
        salespersonLevelDao.insert(salespersonLevelEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(SalespersonLevelUpdateForm updateForm) {
        SalespersonLevelEntity salespersonLevelEntity = SmartBeanUtil.copy(updateForm, SalespersonLevelEntity.class);
        salespersonLevelDao.updateById(salespersonLevelEntity);
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

        salespersonLevelDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Integer salespersonLevelId) {
        if (null == salespersonLevelId){
            return ResponseDTO.ok();
        }

        salespersonLevelDao.deleteById(salespersonLevelId);
        return ResponseDTO.ok();
    }

    /**
     * 导入
     *
     * @param file 上传文件
     * @return 结果
     */
    public ResponseDTO<String> importSalespersonLevel(MultipartFile file) {
        List<SalespersonLevelAddForm> dataList;
        List<SalespersonLevelAddForm> failedDataList = new ArrayList<>();
        List<SalespersonLevelEntity> entityList = new ArrayList<>();

        try {
            dataList = EasyExcel.read(file.getInputStream()).head(SalespersonLevelAddForm.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("数据格式存在问题，无法读取");
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseDTO.userErrorParam("数据为空");
        }

        // 将 SalespersonLevelAddForm 转换为 SalespersonLevelEntity，同时记录失败的数据
        for (SalespersonLevelAddForm form : dataList) {

            // 将有效的记录转换为实体
            SalespersonLevelEntity entity = convertToEntity(form);
            if (entity != null) {
                entityList.add(entity);
            } else {
                // 如果转换失败，记录失败信息
//                form.setErrorMessage("找不到业务员级别");
                failedDataList.add(form);
            }
        }
        // 批量插入有效数据
        List<BatchResult> insert = salespersonLevelDao.insert(entityList);

        // 如果有失败的数据，导出失败记录到 Excel
        if (!failedDataList.isEmpty()) {
            return ResponseDTO.okMsg("成功导入" + insert.size() + "条，失败记录已保存至：" );
        }

        return ResponseDTO.okMsg("成功导入" + insert.size() + "条数据");
    }

    // 将 SalespersonLevelAddForm 转换为 SalespersonLevelEntity
    private SalespersonLevelEntity convertToEntity(SalespersonLevelAddForm form) {
        SalespersonLevelEntity entity = new SalespersonLevelEntity();


        entity.setSalespersonLevelName(form.getSalespersonLevelName());

        return entity;
    }

    /**
     * 导出
     * 需要修改
     */
    public List<SalespersonLevelExcelVO> getAllSalespersonLevel() {
        List<SalespersonLevelEntity> goodsEntityList = salespersonLevelDao.selectList(null);
        return goodsEntityList.stream()
                .map(e ->
                        SalespersonLevelExcelVO.builder()
                                .build()
                )
                .collect(Collectors.toList());

    }

    /**
     * 获取所有业务员
     * @return
     */
    public List<SalespersonLevelVO> getAll() {
        return salespersonLevelDao.getAll();
    }

    public String getSalespersonLevelNameById(String salespersonLevelId) {
        SalespersonLevelEntity salespersonLevel = salespersonLevelDao.selectById(salespersonLevelId);
        if (salespersonLevel == null) {
            return null;
        }
        return salespersonLevel.getSalespersonLevelName();


    }
}
