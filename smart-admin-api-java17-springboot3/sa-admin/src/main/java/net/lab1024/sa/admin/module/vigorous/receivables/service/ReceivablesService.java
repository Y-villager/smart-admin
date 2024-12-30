package net.lab1024.sa.admin.module.vigorous.receivables.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.business.category.service.CategoryService;
import net.lab1024.sa.admin.module.vigorous.customer.service.CustomerService;
import net.lab1024.sa.admin.module.vigorous.receivables.dao.ReceivablesDao;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.entity.ReceivablesEntity;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesAddForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesImportForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesQueryForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesUpdateForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.vo.ReceivablesVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.admin.util.ExcelUtils;
import net.lab1024.sa.admin.util.SplitListUtils;
import net.lab1024.sa.base.common.code.SystemErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.module.support.dict.service.DictService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.dev33.satoken.SaManager.log;

/**
 * 应收单 Service
 *
 * @Author yxz
 * @Date 2024-12-12 14:46:31
 * @Copyright yxz
 */

@Service
public class ReceivablesService {

    @Resource
    private ReceivablesDao receivablesDao;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private SalespersonService salespersonService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DictService dictService;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    private final int BATCH_SIZE = 1000;

    @Value("${file.excel.failed-import.failed-data-name}")
    private String failedDataName;
    @Value("${file.excel.failed-import.upload-path}")
    private String uploadPath;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<ReceivablesVO> queryPage(ReceivablesQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ReceivablesVO> list = receivablesDao.queryPage(page, queryForm);
        PageResult<ReceivablesVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ReceivablesAddForm addForm) {
        ReceivablesEntity receivablesEntity = SmartBeanUtil.copy(addForm, ReceivablesEntity.class);
        receivablesDao.insert(receivablesEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(ReceivablesUpdateForm updateForm) {
        ReceivablesEntity receivablesEntity = SmartBeanUtil.copy(updateForm, ReceivablesEntity.class);
        receivablesDao.updateById(receivablesEntity);
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

        receivablesDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Integer receivablesId) {
        if (null == receivablesId){
            return ResponseDTO.ok();
        }

        receivablesDao.deleteById(receivablesId);
        return ResponseDTO.ok();
    }



    /**
     * 导入
     *
     * @param file 上传文件
     * @return 结果
     */
    public ResponseDTO<String> importReceivables(MultipartFile file, Boolean mode) {
        List<ReceivablesImportForm> dataList;
        List<ReceivablesImportForm> failedDataList = Collections.synchronizedList(new ArrayList<>());

        try {
            dataList = EasyExcel.read(file.getInputStream()).head(ReceivablesImportForm.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("数据格式存在问题，无法读取");
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseDTO.userErrorParam("数据为空");
        }

        List<ReceivablesEntity> entityList = createImportList(dataList, failedDataList, mode);

        // true 为追加模式，false为按单据编号覆盖
        int successTotal = 0;
        try {
            if (mode) {  // 追加
                // 批量插入操作
                List<BatchResult> insert = receivablesDao.insert(entityList);
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
            failed_data_path = ExcelUtils.saveFailedDataToExcel(failedDataList, ReceivablesImportForm.class, uploadPath,failedDataName );
        }

        // 如果有失败的数据，导出失败记录到 Excel
        return ResponseDTO.okMsg("总共"+dataList.size()+"条数据，成功导入" + successTotal + "条，导入失败记录有："+failedDataList.size()+"条", failed_data_path );

    }

    private int doThreadUpdate(List<ReceivablesEntity> entityList) {
//        Long startTime = System.currentTimeMillis();
//        System.out.println("-----------开始多线程------------");
//        System.out.println("CPU核心数："+Runtime.getRuntime().availableProcessors());
        List<ReceivablesEntity> updateList = new ArrayList<>();
        // 初始化线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 50,
                4, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());
        // 将大集合拆分成N个小集合，使用多线程操作数据
        List<List<ReceivablesEntity>> splitList = SplitListUtils.splitList(entityList, 1000);
        // 记录单个任务的执行次数
        CountDownLatch countDownLatch = new CountDownLatch(splitList.size());
        for (List<ReceivablesEntity> subList : splitList) {
            threadPool.execute(new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("当前线程："+ Thread.currentThread().getName());
                    receivablesDao.updateReceivablesByBillNo(subList);
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
//        Long endTime = System.currentTimeMillis();
//        Long costTime = endTime - startTime;
//        System.out.println("----------------总共用时："+costTime+"毫秒");
        return 0;
    }

    @Transactional
    protected int batchUpdate(List<ReceivablesEntity> entityList) {
        int total = 0;
        List<ReceivablesEntity> batchList = new ArrayList<>();  // 用于存储当前批次的记录

        for (int i = 0; i < entityList.size(); i++) {
            batchList.add(entityList.get(i));  // 将当前记录添加到批次列表中

            // 当批次列表达到批量大小或者最后一条记录时，执行批量更新
            if (batchList.size() == BATCH_SIZE || i == entityList.size() - 1) {
                int updateCount = receivablesDao.updateReceivablesByBillNo(batchList);  // 批量更新
                total += updateCount;  // 累计成功更新的记录数

                batchList.clear();  // 清空当前批次列表，准备下一个批次
            }
        }
        return total;
    }

    // 生成导入列表
    private List<ReceivablesEntity> createImportList(List<ReceivablesImportForm> dataList,
                                                     List<ReceivablesImportForm> failedDataList,
                                                     boolean mode) {
        Set<String> billNos = new HashSet<>();
        Set<String> salespersonNames = new HashSet<>();
        Set<String> customerNames = new HashSet<>();

        for (ReceivablesImportForm form : dataList) {
            billNos.add(form.getBillNo());
            salespersonNames.add(form.getSalespersonName());
            customerNames.add(form.getCustomerName());
        }
        // 单据编号 map
        List<ReceivablesVO> receivablesVOList = receivablesDao.queryByBillNos(billNos);
        Map<String, Integer> receivablesMap = receivablesVOList
                .stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(ReceivablesVO::getBillNo, ReceivablesVO::getReceivablesId));

        // 客户映射
        Map<String, Long> customerMap = customerService.queryByCustomerNames(customerNames);
        // 业务员映射
        Map<String, Long> salespersonMap = salespersonService.getSalespersonsByNames(salespersonNames);
        // 币别映射
        Map<String, String> currencyMap = dictService.keyQuery("CURRENCY_TYPE");

        return dataList.parallelStream()
                .map(form -> convertToEntity(form, receivablesMap, salespersonMap, customerMap, currencyMap, failedDataList, mode))
                .filter(Objects::nonNull)
                .toList();

    }

    /**
     * 检验并转换成实体
     * @param form
     * @param receivablesMap
     * @param salespersonMap
     * @param customerMap
     * @param currncyMap
     * @param failedDataList
     * @param mode true覆盖，false追加
     * @return
     */
    private ReceivablesEntity convertToEntity(ReceivablesImportForm form,
                                          Map<String, Integer> receivablesMap,
                                          Map<String, Long> salespersonMap,
                                          Map<String, Long> customerMap,
                                          Map<String, String> currncyMap,
                                          List<ReceivablesImportForm> failedDataList,
                                              boolean mode) {
        ReceivablesEntity entity = new ReceivablesEntity();

        // 根据 mode 的值简化条件判断，true为追加
        if (mode){
            if (receivablesMap.containsKey(form.getBillNo())){
                form.setErrorMsg("追加模式: 系统已存在系统单据编号的数据");
                failedDataList.add(form);
                return null;
            }
        }else {
            if (!receivablesMap.containsKey(form.getBillNo())){
                form.setErrorMsg("覆盖模式：系统中不存在该单据编号的数据");
                failedDataList.add(form);
                return null;
            }
        }

        if (form.getSalespersonName()==null){
            form.setErrorMsg("业务员为空");
            failedDataList.add(form);
            return null;
        }
        if (!salespersonMap.containsKey(form.getSalespersonName())){
            form.setErrorMsg("系统不存在该业务员");
            failedDataList.add(form);
            return null;
        }
        if (!customerMap.containsKey(form.getCustomerName())){
            form.setErrorMsg("系统不存在该客户");
            failedDataList.add(form);
            return null;
        }

        // 获取编码
        String currencyType = currncyMap.get(form.getCurrencyType());
        if (currencyType == null){
            form.setErrorMsg("币别系统不存在："+form.getCurrencyType());
            failedDataList.add(form);
            return null;
        }

        if (form.getReceivablesDate().contains("-")){
            LocalDate date = LocalDate.parse(form.getReceivablesDate());
            entity.setReceivablesDate(date);
        }else if (form.getReceivablesDate().contains("/")){
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/M/d");
            LocalDate date = LocalDate.parse(form.getReceivablesDate(), fmt);
            entity.setReceivablesDate(date);
        }

        entity.setBillNo(form.getBillNo());
        entity.setOriginBillNo(form.getOriginBillNo());
        entity.setCustomerId( customerMap.get(form.getCustomerName()));
        entity.setSalespersonId(salespersonMap.get(form.getSalespersonName()));
        entity.setCurrencyType(currencyType);
        entity.setAmount(form.getAmount());
        entity.setRate(form.getRate());

        return entity;
    }

    // 将 ReceivablesAddForm 转换为 ReceivablesEntity


    /**
     * 导出
     * 需要修改
     */
    public List<ReceivablesVO> getAllReceivables() {
        List<ReceivablesEntity> entityList = receivablesDao.selectList(null);
//        return entityList.stream()
//                .map(e ->
//                        ReceivablesVO.builder()
//                                .build()
//                )
//                .collect(Collectors.toList());
        return null;

    }
}
