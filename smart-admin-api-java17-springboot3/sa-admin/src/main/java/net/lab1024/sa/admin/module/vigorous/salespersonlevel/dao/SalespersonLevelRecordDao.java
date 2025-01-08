package net.lab1024.sa.admin.module.vigorous.salespersonlevel.dao;

import java.util.List;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.entity.SalespersonLevelRecordEntity;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelRecordQueryForm;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.vo.SalespersonLevelRecordVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 业务员级别变动记录 Dao
 *
 * @Author yxz
 * @Date 2025-01-07 08:58:41
 * @Copyright (c)2024 yxz
 */

@Mapper
@Component
public interface SalespersonLevelRecordDao extends BaseMapper<SalespersonLevelRecordEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<SalespersonLevelRecordVO> queryPage(Page page, @Param("queryForm") SalespersonLevelRecordQueryForm queryForm);


}
