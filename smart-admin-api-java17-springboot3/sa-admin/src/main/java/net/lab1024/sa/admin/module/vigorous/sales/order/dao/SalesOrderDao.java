package net.lab1024.sa.admin.module.vigorous.sales.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.vigorous.sales.order.domain.form.SalesOrderQueryForm;
import net.lab1024.sa.admin.module.vigorous.sales.order.domain.entity.SalesOrderEntity;
import net.lab1024.sa.admin.module.vigorous.sales.order.domain.vo.SalesOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 销售订单表 Dao
 *
 * @Author yxz
 * @Date 2025-10-23 14:10:32
 * @Copyright (c)2024 yxz
 */

@Mapper
@Component
public interface SalesOrderDao extends BaseMapper<SalesOrderEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<SalesOrderVO> queryPage(Page page, @Param("queryForm") SalesOrderQueryForm queryForm);


    /**
     * 批量更新
     * @param entityList
     * @return
     */
    int batchUpdate(@Param("list") List<?> entityList);
}
