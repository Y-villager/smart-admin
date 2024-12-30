package net.lab1024.sa.admin.module.vigorous.commission.rule.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.vigorous.commission.rule.dao.CommissionRuleDao;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.entity.CommissionRuleEntity;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.form.CommissionRuleAddForm;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.form.CommissionRuleQueryForm;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.form.CommissionRuleUpdateForm;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.vo.CommissionRuleVO;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.service.SalespersonLevelService;
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
import java.util.ArrayList;
import java.util.List;

import static cn.dev33.satoken.SaManager.log;

/**
 * 提成规则 Service
 *
 * @Author yxz
 * @Date 2024-12-23 16:14:00
 * @Copyright (c)2024 yxz
 */

@Service
public class CommissionRuleService {

    @Resource
    private CommissionRuleDao commissionRuleDao;
    @Autowired
    private SalespersonLevelService salespersonLevelService;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<CommissionRuleVO> queryPage(CommissionRuleQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<CommissionRuleVO> list = commissionRuleDao.queryPage(page, queryForm);
        list.forEach(e->{
            e.setSalespersonLevelName(salespersonLevelService.getSalespersonLevelNameById(e.getSalespersonLevelId()));
        });
        PageResult<CommissionRuleVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(CommissionRuleAddForm addForm) {
        CommissionRuleEntity commissionRuleEntity = SmartBeanUtil.copy(addForm, CommissionRuleEntity.class);
        commissionRuleDao.insert(commissionRuleEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(CommissionRuleUpdateForm updateForm) {
        CommissionRuleEntity commissionRuleEntity = SmartBeanUtil.copy(updateForm, CommissionRuleEntity.class);
        commissionRuleDao.updateById(commissionRuleEntity);
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

        commissionRuleDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long ruleId) {
        if (null == ruleId){
            return ResponseDTO.ok();
        }

        commissionRuleDao.deleteById(ruleId);
        return ResponseDTO.ok();
    }

    /**
     * 导入
     *
     * @param file 上传文件
     * @return 结果
     */
    public ResponseDTO<String> importCommissionRule(MultipartFile file) {
        List<CommissionRuleAddForm> dataList;
        List<CommissionRuleAddForm> failedDataList = new ArrayList<>();
        List<CommissionRuleEntity> entityList = new ArrayList<>();

        try {
            dataList = EasyExcel.read(file.getInputStream()).head(CommissionRuleAddForm.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("数据格式存在问题，无法读取");
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseDTO.userErrorParam("数据为空");
        }

        // 将 CommissionRuleAddForm 转换为 CommissionRuleEntity，同时记录失败的数据
        for (CommissionRuleAddForm form : dataList) {

            // 将有效的记录转换为实体
            CommissionRuleEntity entity = convertToEntity(form);
            if (entity != null) {
                entityList.add(entity);
            } else {
                // 如果转换失败，记录失败信息
                failedDataList.add(form);
            }
        }
        // 批量插入有效数据
        List<BatchResult> insert = commissionRuleDao.insert(entityList);

        // 如果有失败的数据，导出失败记录到 Excel
        return ResponseDTO.okMsg("总共"+dataList.size()+"条数据，成功导入" + insert.get(0).getParameterObjects().size() + "条，导入失败记录有："+failedDataList.size()+"条" );

    }

    // 将 CommissionRuleAddForm 转换为 CommissionRuleEntity
    private CommissionRuleEntity convertToEntity(CommissionRuleAddForm form) {
        CommissionRuleEntity entity = new CommissionRuleEntity();


  //      entity.setruleId(form.getruleId());
  //      entity.setcurrencyType(form.getcurrencyType());
  //      entity.setsalespersonLevelId(form.getsalespersonLevelId());
  //      entity.setcommissionRate(form.getcommissionRate());
  //      entity.setfirstOrderRate(form.getfirstOrderRate());
  //      entity.setfirstYearRate(form.getfirstYearRate());
  //      entity.setyearlyDecreaseRate(form.getyearlyDecreaseRate());
  //      entity.setminRate(form.getminRate());
  //      entity.setremark(form.getremark());

        return entity;
    }

    /**
     * 导出
     * 需要修改
     */
    public List<CommissionRuleVO> getAllCommissionRule() {
        List<CommissionRuleEntity> entityList = commissionRuleDao.selectList(null);
//        return entityList.stream()
//                .map(e ->
//                        CommissionRuleVO.builder()
//                                .build()
//                )
//                .collect(Collectors.toList());
        return null;

    }
}
