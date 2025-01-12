package net.lab1024.sa.admin.module.vigorous.commission.calc.dao;

import java.util.List;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.entity.CommissionRecordEntity;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.form.CommissionRecordQueryForm;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.vo.CommissionRecordVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 业务提成记录 Dao
 *
 * @Author yxz
 * @Date 2025-01-12 15:25:35
 * @Copyright (c)2024 yxz
 */

@Mapper
@Component
public interface CommissionRecordDao extends BaseMapper<CommissionRecordEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<CommissionRecordVO> queryPage(Page page, @Param("queryForm") CommissionRecordQueryForm queryForm);


}
