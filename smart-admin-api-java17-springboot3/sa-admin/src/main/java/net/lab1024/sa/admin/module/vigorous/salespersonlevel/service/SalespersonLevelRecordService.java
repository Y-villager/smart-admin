package net.lab1024.sa.admin.module.vigorous.salespersonlevel.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.dao.SalespersonLevelRecordDao;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.entity.SalespersonLevelRecordEntity;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelRecordAddForm;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelRecordQueryForm;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelRecordUpdateForm;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.vo.SalespersonLevelRecordVO;
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

import static cn.dev33.satoken.SaManager.log;

/**
 * 业务员级别变动记录 Service
 *
 * @Author yxz
 * @Date 2025-01-07 08:58:41
 * @Copyright (c)2024 yxz
 */

@Service
public class SalespersonLevelRecordService {

    @Resource
    private SalespersonLevelRecordDao salespersonLevelRecordDao;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<SalespersonLevelRecordVO> queryPage(SalespersonLevelRecordQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<SalespersonLevelRecordVO> list = salespersonLevelRecordDao.queryPage(page, queryForm);
        PageResult<SalespersonLevelRecordVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(SalespersonLevelRecordAddForm addForm) {
        SalespersonLevelRecordEntity entity = SmartBeanUtil.copy(addForm, SalespersonLevelRecordEntity.class);
        salespersonLevelRecordDao.insert(entity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(SalespersonLevelRecordUpdateForm updateForm) {
        SalespersonLevelRecordEntity salespersonLevelRecordEntity = SmartBeanUtil.copy(updateForm, SalespersonLevelRecordEntity.class);
        salespersonLevelRecordDao.updateById(salespersonLevelRecordEntity);
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
        salespersonLevelRecordDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Integer id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        salespersonLevelRecordDao.deleteById(id);
        return ResponseDTO.ok();
    }


    /**
     * 导入
     *
     * @param file 上传文件
     * @return 结果
     */
    public ResponseDTO<String> importSalespersonLevelRecord(MultipartFile file) {
        List<SalespersonLevelRecordAddForm> dataList;
        List<SalespersonLevelRecordAddForm> failedDataList = new ArrayList<>();
        List<SalespersonLevelRecordEntity> entityList = new ArrayList<>();

        try {
            dataList = EasyExcel.read(file.getInputStream()).head(SalespersonLevelRecordAddForm.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("数据格式存在问题，无法读取");
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseDTO.userErrorParam("数据为空");
        }

        // 将 SalespersonLevelRecordAddForm 转换为 SalespersonLevelRecordEntity，同时记录失败的数据
        for (SalespersonLevelRecordAddForm form : dataList) {

            // 将有效的记录转换为实体
            SalespersonLevelRecordEntity entity = convertToEntity(form);
            if (entity != null) {
                entityList.add(entity);
            } else {
                // 如果转换失败，记录失败信息
                failedDataList.add(form);
            }
        }
        // 批量插入有效数据
        List<BatchResult> insert = salespersonLevelRecordDao.insert(entityList);

        // 如果有失败的数据，导出失败记录到 Excel
        return ResponseDTO.okMsg("总共"+dataList.size()+"条数据，成功导入" + insert.get(0).getParameterObjects().size() + "条，导入失败记录有："+failedDataList.size()+"条" );

    }

    // 将 SalespersonLevelRecordAddForm 转换为 SalespersonLevelRecordEntity
    private SalespersonLevelRecordEntity convertToEntity(SalespersonLevelRecordAddForm form) {
        SalespersonLevelRecordEntity entity = new SalespersonLevelRecordEntity();


  //      entity.setid(form.getid());
  //      entity.setsalespersonId(form.getsalespersonId());
  //      entity.setoldLevel(form.getoldLevel());
  //      entity.setnewLevel(form.getnewLevel());
  //      entity.setstartDate(form.getstartDate());
  //      entity.setendDate(form.getendDate());
  //      entity.setchangeReason(form.getchangeReason());
  //      entity.setcreateTime(form.getcreateTime());

        return entity;
    }

    /**
     * 导出
     * 需要修改
     */
    public List<SalespersonLevelRecordVO> getAllSalespersonLevelRecord() {
        List<SalespersonLevelRecordEntity> entityList = salespersonLevelRecordDao.selectList(null);
//        return entityList.stream()
//                .map(e ->
//                        SalespersonLevelRecordVO.builder()
//                                .build()
//                )
//                .collect(Collectors.toList());
        return null;

    }


    public void updateLevel(SalespersonLevelRecordUpdateForm updateForm) {
    }
}
