package net.lab1024.sa.admin.module.vigorous.receivables.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.vigorous.receivables.dao.ReceivablesDetailsDao;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.entity.ReceivablesDetailsEntity;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesDetailsAddForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesDetailsImportForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesDetailsQueryForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesDetailsUpdateForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.vo.ReceivablesDetailsVO;
import net.lab1024.sa.admin.util.BatchUtils;
import net.lab1024.sa.admin.util.ExcelUtils;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.dev33.satoken.SaManager.log;


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
        List<ReceivablesDetailsVO> entityList = receivablesDetailsDao.queryPage(null,queryForm);
        return entityList.stream()
                .map(e ->
                        ReceivablesDetailsVO.builder()
                                .originBillNo(e.getOriginBillNo())
                                .materialCode(e.getMaterialCode())
                                .materialName(e.getMaterialName())
                                .serialBatch(e.getSerialBatch())
                                .saleUnit(e.getSaleUnit())
                                .saleQuantity(e.getSaleQuantity())
                                .build()
                )
                .collect(Collectors.toList());
    }


    public ResponseDTO<String> importReceivablesDetails(MultipartFile file, Boolean mode) {
        List<ReceivablesDetailsImportForm> dataList;
        List<ReceivablesDetailsImportForm> failedDataList = Collections.synchronizedList(new ArrayList<>());

        try {
            dataList = EasyExcel.read(file.getInputStream()).head(ReceivablesDetailsImportForm.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("数据格式存在问题，无法读取");
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseDTO.userErrorParam("数据为空");
        }

        List<ReceivablesDetailsEntity> entityList = createImportList(dataList, failedDataList, mode);

        int successTotal = 0;
        if(entityList != null && !entityList.isEmpty()){
            if (mode) {  // 追加
                // 批量插入操作
                List<BatchResult> insert = receivablesDetailsDao.insert(entityList);
                for (BatchResult batchResult : insert) {
                    successTotal += batchResult.getParameterObjects().size();
                }
            } else {  // 覆盖
                // 执行批量更新操作
                successTotal = batchUtils.doThreadInsertOrUpdate(entityList, receivablesDetailsDao, "batchUpdate", true);
//                if (successTotal != entityList.size()) {
//                    return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "系统出错，请联系管理员。");
//                }
            }
        }

        String failed_data_path=null;
        if (!failedDataList.isEmpty()) {
            // 创建并保存失败的数据文件
            failed_data_path = ExcelUtils.saveFailedDataToExcel(failedDataList, ReceivablesDetailsImportForm.class);
        }

        // 如果有失败的数据，导出失败记录到 Excel
        return ResponseDTO.okMsg("总共"+dataList.size()+"条数据，成功导入" + successTotal + "条，导入失败记录有："+failedDataList.size()+"条", failed_data_path );

    }

    /**
     *
     * @param dataList
     * @param failedDataList
     * @param mode 0 False覆盖; 1 True追加
     * @return
     */
    private List<ReceivablesDetailsEntity> createImportList(List<ReceivablesDetailsImportForm> dataList,
                                                    List<ReceivablesDetailsImportForm> failedDataList,
                                                    boolean mode) {
        // 应收物料明细
        List<ReceivablesDetailsEntity> entityList = new ArrayList<>();
//        receivablesDetailsDao.getExistingBillNo()
        // 1. 先查询数据库中已存在的记录
        List<String> existingKeys = receivablesDetailsDao.getExistingUniqueKeys(dataList);
        Set<String> existingKeySet = new HashSet<>(existingKeys);

        for (ReceivablesDetailsImportForm importForm : dataList) {
            if (importForm.getOriginBillNo()==null || importForm.getMaterialCode()==null || importForm.getSerialNum()==null){
                importForm.setErrorMsg("缺少相关数据");
                failedDataList.add(importForm);
                continue;
            }
            // 生成唯一键
            String uniqueKey = generateUniqueKey(
                    importForm.getOriginBillNo(),
                    importForm.getMaterialCode(),
                    importForm.getSerialNum()
            );
            // 数据库存在唯一键
            if (existingKeySet.contains(uniqueKey) && mode){
                importForm.setErrorMsg("【追加】数据库已存在，不允许插入");
                failedDataList.add(importForm);
                continue;
            }

            ReceivablesDetailsEntity entity = convertToEntity(importForm);
            entityList.add(entity);
        }

        return entityList;
    }

    // 生成唯一键的方法
    private String generateUniqueKey(String originBillNo, String materialCode, Integer serialNum) {
        return originBillNo + "|" + materialCode + "|" + serialNum;
    }

    private ReceivablesDetailsEntity convertToEntity(ReceivablesDetailsImportForm form) {
        ReceivablesDetailsEntity entity = new ReceivablesDetailsEntity();

        entity.setOriginBillNo(form.getOriginBillNo().trim()); // 源单
        entity.setMaterialCode(form.getMaterialCode());  // 物料编码
        entity.setMaterialName(form.getMaterialName());  // 物料编码
        entity.setSerialNum(form.getSerialNum());  // 序号
        entity.setSaleUnit(form.getSaleUnit());         // 单位
        entity.setSaleQuantity(form.getSaleQuantity()); // 数量
        entity.setSaleAmount(form.getSalesAmount()); // 数量

        // 系统字段
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());

        return entity;
    }

    public Map<String, List<ReceivablesDetailsEntity>> queryByBillNos(Set<String> allBillNos) {
       List<ReceivablesDetailsEntity> list = receivablesDetailsDao.queryByBillNos(allBillNos);

        // 2. 在Java中按originBillNo分组
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream()
                .collect(Collectors.groupingBy(
                        ReceivablesDetailsEntity::getOriginBillNo,
                        LinkedHashMap::new,  // 保持插入顺序
                        Collectors.toList()
                ));
    }
}
