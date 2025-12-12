package net.lab1024.sa.admin.module.vigorous.fsales.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.vigorous.fsales.domain.entity.FSalesEntity;
import net.lab1024.sa.admin.module.vigorous.fsales.domain.form.FSalesQueryForm;
import net.lab1024.sa.admin.module.vigorous.fsales.domain.vo.FSalesVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 发货通知单 Dao
 *
 * @Author yxz
 * @Date 2025-10-23 14:11:35
 * @Copyright (c)2024 yxz
 */

@Mapper
@Component
public interface FSalesDao extends BaseMapper<FSalesEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<FSalesVO> queryPage(Page page, @Param("queryForm") FSalesQueryForm queryForm);


    /**
   * 批量更新
   */
    int batchUpdate(@Param("list") List<?> entityList);

    Set<String> getExistingBillNo(@Param("billNos") Set<String> billNos);

    List<String> queryByBillNos(Set<String> billNos);
}
