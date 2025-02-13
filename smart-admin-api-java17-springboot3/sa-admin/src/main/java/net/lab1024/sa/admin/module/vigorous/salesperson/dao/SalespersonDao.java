package net.lab1024.sa.admin.module.vigorous.salesperson.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.dto.SalespersonDto;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.entity.SalespersonEntity;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonQueryForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.vo.SalespersonVO;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelRecordAddForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 业务员 Dao
 *
 * @Author yxz
 * @Date 2024-12-16 10:56:45
 * @Copyright (c)2024 yxz
 */

@Mapper
@Component
public interface SalespersonDao extends BaseMapper<SalespersonEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<SalespersonVO> queryPage(Page page, @Param("queryForm") SalespersonQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);


    /*
    * 获取所有业务员编码
    * */
    Set<String> getAllSalespersonCodes();

    List<Long> getSalespersonIdByName(@Param("salespersonName") String salespersonName);

    String getSalespersonNameById(@Param("salespersonId") Long salespersonId);

    Collection<SalespersonVO> getSalespersonsByNames(@Param("salespersonNames") Set<String> salespersonNames);

    Collection<SalespersonVO> getSalespersonNamesByIds(@Param("salespersonIds") Set<Long> salespersonIds);

    void updateLevel(@Param("form") SalespersonLevelRecordAddForm updateForm);

    int getNowSalespersonLevel(@Param("id") Long salespersonId);

    List<SalespersonDto> getAllSalesperson();
}
