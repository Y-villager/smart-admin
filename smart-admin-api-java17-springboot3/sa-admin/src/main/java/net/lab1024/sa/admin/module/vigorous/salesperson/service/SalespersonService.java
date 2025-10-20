package net.lab1024.sa.admin.module.vigorous.salesperson.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.system.department.service.DepartmentService;
import net.lab1024.sa.admin.module.vigorous.salesperson.dao.SalespersonDao;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.dto.SalespersonDto;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.entity.SalespersonEntity;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonAddForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonImportForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonQueryForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonUpdateForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.vo.SalespersonExcelVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.vo.SalespersonVO;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelRecordAddForm;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.service.SalespersonLevelRecordService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private SalespersonLevelRecordService salespersonLevelRecordService;

//    private static final String REDIS_KEY = "salesperson_list";

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redisTemplate;

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

        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    @CacheEvict(value = "salesperson", key = "'list'")
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
    @CacheEvict(value = "salesperson", key = "'list'")
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
    @CacheEvict(value = "salesperson", key = "'list'")
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
    @CacheEvict(value = "salesperson", key = "'list'")
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
                log.error(form.getSalespersonName()+" ---找不到部门");
                failedDataList.add(form);
            }
        }
        // 批量插入有效数据
        List<BatchResult> insert = salespersonDao.insert(entityList);

        // 如果有失败的数据，导出失败记录到 Excel
        if (!failedDataList.isEmpty()) {
            return ResponseDTO.okMsg("成功导入" + insert.get(0).getParameterObjects().size() + "条，导入失败记录有："+failedDataList.size()+"条" );
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
            return null;
        }
        entity.setDepartmentId(departmentId);

        return entity;
    }

    /**
     * 导出
     */
    public List<SalespersonExcelVO> exportSalespersons() {
        List<SalespersonEntity> goodsEntityList = salespersonDao.selectList(null);
        List<SalespersonDto> allSalesperson = getAllSalesperson();
        return goodsEntityList.stream()
                .map(e ->
                        SalespersonExcelVO.builder()
                                .salespersonCode(e.getSalespersonCode())
                                .salespersonName(e.getSalespersonName())
                                .department(departmentService.queryDepartmentName(e.getDepartmentId()))
                                .build()
                )
                .collect(Collectors.toList());

    }


    /*
    * 根据名称获取业务员id
    * */
    public List<Long> getSalespersonIdByName(String salespersonName) {
        return salespersonDao.getSalespersonIdByName(salespersonName);
    }

    public Long getIdBySalespersonName(String salespersonName) {
        if (salespersonName == null) {return null;}
        List<Long> names = salespersonDao.getSalespersonIdByName(salespersonName);
        if (names==null || names.size()!=1) {
            return -1L;
        }
        return names.get(0);
    }



    public String getSalespersonNameById(Long salespersonId) {
        return salespersonDao.getSalespersonNameById(salespersonId);
    }


    public SalespersonEntity queryById(Long salespersonId) {
        return salespersonDao.selectById(salespersonId);
    }

    public Map<String, Long> getSalespersonsByNames(Set<String> salespersonNames) {
        return salespersonDao.getSalespersonsByNames(salespersonNames)
                .stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(SalespersonVO::getSalespersonName, SalespersonVO::getId));
    }



    public Map<Long, String> getSalespersonNamesByIds(Set<Long> salespersonIds) {
        if (salespersonIds == null || salespersonIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return salespersonDao.getSalespersonNamesByIds(salespersonIds).stream()
                .collect(Collectors.toMap(SalespersonVO::getId, SalespersonVO::getSalespersonName));
    }

    @Transactional
    public ResponseDTO<String> updateLevel(SalespersonLevelRecordAddForm form) {
        // 查询之前变动记录
        salespersonDao.updateLevel(form);
        salespersonLevelRecordService.add(form);
        return ResponseDTO.ok();
    }

    /**
     * 查询所有业务员（自动缓存）
     * @return
     */
    @Cacheable(value = "salesperson", key = "'list'")
    public List<SalespersonDto> getAllSalesperson() {
        // 直接查询数据库，Spring Cache 会自动缓存结果
        return salespersonDao.getAllSalesperson();
    }
//    @Cacheable(value = "salesperson", key = "'list'")
//    public List<SalespersonDto> getAllSalesperson() {
//        List<SalespersonDto> salespersonList = getSalespersonFromRedis();
//
//        if (!CollectionUtils.isEmpty(salespersonList)) {
//            // 如果redis有数据，直接返回
//            return salespersonList;
//        }
//
//        salespersonList = salespersonDao.getAllSalesperson();
//
//        if (!CollectionUtils.isEmpty(salespersonList)) {
//            saveToRedis(salespersonList);
//        }
//        return salespersonList;
//    }

    @Cacheable(value = "salesperson", key = "'map'")
    public Map<Long, String> getSalespersonIdNameMap() {
        List<SalespersonDto> allSalesperson = getAllSalesperson();

        // 创建一个 Map，用于存储 id 和 name 的对应关系
        Map<Long, String> idToNameMap = new HashMap<>();
        // 遍历所有 SalespersonDto，将 id 和 name 放入 Map 中
        for (SalespersonDto dto : allSalesperson) {
            idToNameMap.put(dto.getId(), dto.getName());
        }

        return idToNameMap;
    }

    // 将数据存入 Redis 中，并设置过期时间（例如 1 天）
//    private void saveToRedis(List<SalespersonDto> salespersonList) {
//        String cacheKey = REDIS_KEY;
//        String redisData = JSON.toJSONString(salespersonList);  // 使用 JSON 序列化
//        redisTemplate.opsForValue().set(cacheKey, redisData, 1, TimeUnit.DAYS);  // 设置缓存过期时间
//    }

    /**
     * 获取业务员缓存列表
     * @return
     */
//    private List<SalespersonDto> getSalespersonFromRedis() {
//        String cacheKey = REDIS_KEY;
//        String redisData = (String) redisTemplate.opsForValue().get(cacheKey);
//        if (redisData != null){
//            return JSON.parseArray(redisData, SalespersonDto.class);
//        }
//        return null;
//    }

    public ResponseDTO<String> updateDisabledFlag(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }
        SalespersonEntity salesperson = salespersonDao.selectById(id);
        if (null == salesperson){
            return ResponseDTO.ok();
        }
        salespersonDao.updateDisabledFlag(id, !salesperson.getDisabledFlag());

        return ResponseDTO.ok();
    }

    public boolean isSubordinate(Long salespersonId, Long targetId) {
        return salespersonDao.isSubordinate(salespersonId, targetId);
    }
}
