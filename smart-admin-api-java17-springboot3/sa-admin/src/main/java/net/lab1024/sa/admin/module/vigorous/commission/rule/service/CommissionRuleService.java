package net.lab1024.sa.admin.module.vigorous.commission.rule.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.enumeration.CommissionTypeEnum;
import net.lab1024.sa.admin.module.vigorous.commission.rule.dao.CommissionRuleDao;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.entity.CommissionRuleEntity;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.form.CommissionRuleAddForm;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.form.CommissionRuleImportForm;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.form.CommissionRuleQueryForm;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.form.CommissionRuleUpdateForm;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.vo.CommissionRuleVO;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.dto.SalesCommissionDto;
import net.lab1024.sa.admin.util.ExcelUtils;
import net.lab1024.sa.admin.util.SplitListUtils;
import net.lab1024.sa.base.common.code.SystemErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private CommissionRuleCacheService commissionRuleCacheService;


    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<CommissionRuleVO> queryPage(CommissionRuleQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<CommissionRuleVO> list = commissionRuleDao.queryPage(page, queryForm);
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
    public ResponseDTO<String> importCommissionRule(MultipartFile file, Boolean mode) {
        List<CommissionRuleImportForm> dataList;
        List<CommissionRuleImportForm> failedDataList = new ArrayList<>();

        try {
            dataList = EasyExcel.read(file.getInputStream()).head(CommissionRuleImportForm.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("数据格式存在问题，无法读取");
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseDTO.userErrorParam("数据为空");
        }

        // 将 CommissionRuleImportForm 转换为 CommissionRuleEntity，同时记录失败的数据
        List<CommissionRuleEntity> entityList = createImportList(dataList, failedDataList, mode);
        // true 为追加模式，false为按单据编号覆盖
        int successTotal = 0;
        try {
            if (mode) {  // 追加
                // 批量插入操作
                List<BatchResult> insert = commissionRuleDao.insert(entityList);
                for (BatchResult batchResult : insert) {
                    successTotal += batchResult.getParameterObjects().size();
                }
            } else {  // 覆盖
                // 执行批量更新操作
                successTotal = doThreadUpdate(entityList);
            }
        }
        catch (DataAccessException e) {
            // 捕获数据库访问异常
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "数据库操作失败，更新或插入过程中出现异常："+e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "发生未知错误："+e.getMessage());
        }

        String failed_data_path=null;
        if (!failedDataList.isEmpty()) {
            // 创建并保存失败的数据文件
            failed_data_path = ExcelUtils.saveFailedDataToExcel(failedDataList, CommissionRuleImportForm.class);
        }

        // 如果有失败的数据，导出失败记录到 Excel
        return ResponseDTO.okMsg("总共"+dataList.size()+"条数据，成功导入" + successTotal + "条，导入失败记录有："+failedDataList.size()+"条", failed_data_path );

    }

    // 生成导入列表
    private List<CommissionRuleEntity> createImportList(List<CommissionRuleImportForm> dataList,
                                                        List<CommissionRuleImportForm> failedDataList,
                                                        boolean mode) {

        for (CommissionRuleImportForm form : dataList) {
        }
        // 单据编号 map

        // 需要的映射数据

        return dataList.parallelStream()
                .map(form -> convertToEntity(form, failedDataList, mode))
                .filter(Objects::nonNull)
                .toList();

    }

    // 将 CommissionRuleImportForm 转换为 CommissionRuleEntity
    private CommissionRuleEntity convertToEntity(CommissionRuleImportForm form,
                                                 List<CommissionRuleImportForm> failedDataList,
                                                 boolean mode) {
        CommissionRuleEntity entity = new CommissionRuleEntity();


        //      entity.setruleId(form.getruleId());
        //      entity.settransferStatus(form.gettransferStatus());
        //      entity.setcustomerGroup(form.getcustomerGroup());
        //      entity.setcommissionType(form.getcommissionType());
        //      entity.setuseDynamicFormula(form.getuseDynamicFormula());
        //      entity.setcommissionRate(form.getcommissionRate());
        //      entity.setformulaId(form.getformulaId());
        //      entity.setremark(form.getremark());
        //      entity.setcreateTime(form.getcreateTime());
        //      entity.setupdateTime(form.getupdateTime());

        return entity;
    }

    /**
     * 导出
     * 需要修改
     */
    public List<CommissionRuleVO> exportCommissionRule(CommissionRuleQueryForm queryForm) {
        //List<CommissionRuleVO> entityList = commissionRuleDao.selectList(null);
        List<CommissionRuleVO> entityList = commissionRuleDao.queryPage(null,queryForm);
//        return entityList.stream()
//                .map(e ->
//                        CommissionRuleVO.builder()
//                                .build()
//                )
//                .collect(Collectors.toList());
        return null;

    }

    private int doThreadUpdate(List<CommissionRuleEntity> entityList) {
        List<CommissionRuleEntity> updateList = new ArrayList<>();
        // 初始化线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 50,
                4, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());
        // 将大集合拆分成N个小集合，使用多线程操作数据
        List<List<CommissionRuleEntity>> splitList = SplitListUtils.splitList(entityList, 1000);
        // 记录单个任务的执行次数
        CountDownLatch countDownLatch = new CountDownLatch(splitList.size());
        for (List<CommissionRuleEntity> subList : splitList) {
            threadPool.execute(new Thread(new Runnable() {
                @Override
                public void run() {
//                    commissionRuleDao.updateCommissionRuleByBillNo(subList);
                    countDownLatch.countDown();
                }
            }));
        }
        try {
            // 让当前线程处于阻塞状态，知道锁存器计数为零
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public CommissionRuleVO queryCommissionRule(CommissionRuleVO commissionRule) {
        return commissionRuleDao.queryCommissionRule(commissionRule);
    }

    // 从缓存中获取提成规则
    public CommissionRuleVO queryCommissionRuleFromCache(SalesCommissionDto salesOutbound, CommissionTypeEnum commissionTypeEnum) {
        // 提供转交状态、客户分组和提成类型作为查询条件
        int transferStatus = salesOutbound.getTransferStatus() != null ? (salesOutbound.getTransferStatus() != 0 ? 1 : 0) : 0;
        Integer customerGroup = salesOutbound.getCustomerGroup();
        if (customerGroup==null){
            return null;
        }
        // 查询 Redis 中的提成规则
        return commissionRuleCacheService.getCommissionRuleFromCache(transferStatus, customerGroup, commissionTypeEnum.getValue());
    }



}
