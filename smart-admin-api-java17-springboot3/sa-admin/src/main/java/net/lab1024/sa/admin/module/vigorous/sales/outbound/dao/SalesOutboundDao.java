package net.lab1024.sa.admin.module.vigorous.sales.outbound.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.entity.SalesOutboundEntity;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.SalesOutboundQueryForm;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo.SalesOutboundVO;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo.SalesOutboundVO2;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 销售出库 Dao
 *
 * @Author yxz
 * @Date 2024-12-12 14:48:19
 * @Copyright (c)2024 yxz
 */

@Mapper
@Component
public interface SalesOutboundDao extends BaseMapper<SalesOutboundEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<SalesOutboundVO> queryPage(Page page, @Param("queryForm") SalesOutboundQueryForm queryForm);

    List<SalesOutboundVO2> queryPage2(Page page, @Param("queryForm") SalesOutboundQueryForm queryForm);

    /**
     * 查询 导出列表
     * @param queryForm
     * @return
     */
    List<SalesOutboundVO> queryExportList(@Param("queryForm") SalesOutboundQueryForm queryForm);


    SalesOutboundEntity queryByBillNo(@Param("billNo") String billNo);

    SalesOutboundVO queryFirstOrderOfCustomer(@Param("customerId") Long customerId);

    List<SalesOutboundEntity> queryByBillNos(@Param("billNos") Set<String> billNos);

    /**
     * 连接查询
     * @param page
     * @param queryForm
     * @return
     */
    List<SalesOutboundVO> queryPageWithReceivables(Page<?> page, SalesOutboundQueryForm queryForm);
}
