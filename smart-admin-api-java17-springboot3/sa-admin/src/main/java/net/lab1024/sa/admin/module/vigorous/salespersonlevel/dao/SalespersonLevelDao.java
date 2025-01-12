package net.lab1024.sa.admin.module.vigorous.salespersonlevel.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.common.dto.GenericDTO;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.entity.SalespersonLevelEntity;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelQueryForm;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.vo.SalespersonLevelVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 业务员级别 Dao
 *
 * @Author yxz
 * @Date 2024-12-14 16:07:14
 * @Copyright (c)2024 yxz
 */

@Mapper
@Component
public interface SalespersonLevelDao extends BaseMapper<SalespersonLevelEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<SalespersonLevelVO> queryPage(Page page, @Param("queryForm") SalespersonLevelQueryForm queryForm);


    List<GenericDTO> getAllDto();
}
