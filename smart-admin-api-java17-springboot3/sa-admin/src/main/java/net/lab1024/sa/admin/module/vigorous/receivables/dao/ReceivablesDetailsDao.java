package net.lab1024.sa.admin.module.vigorous.receivables.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.entity.ReceivablesDetailsEntity;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesDetailsQueryForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.vo.ReceivablesDetailsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 应收明细表 Dao
 *
 * @Author yxz
 * @Date 2025-10-23 14:43:51
 * @Copyright (c)2025 yxz
 */

@Mapper
@Component
public interface ReceivablesDetailsDao extends BaseMapper<ReceivablesDetailsEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<ReceivablesDetailsVO> queryPage(Page page, @Param("queryForm") ReceivablesDetailsQueryForm queryForm);


    /**
   * 批量更新
   */
    int batchInsertOrUpdate(@Param("list") List<?> entityList);


    int batchUpdate(@Param("list") List<?> entityList);

    Set<String> getExistingBillNo(@Param("billNos") Set<String> billNos);

}
