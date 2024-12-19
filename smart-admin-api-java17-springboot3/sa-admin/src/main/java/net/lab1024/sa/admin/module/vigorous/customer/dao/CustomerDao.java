package net.lab1024.sa.admin.module.vigorous.customer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.vigorous.customer.domain.entity.CustomerEntity;
import net.lab1024.sa.admin.module.vigorous.customer.domain.form.CustomerQueryForm;
import net.lab1024.sa.admin.module.vigorous.customer.domain.vo.CustomerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 客户 Dao
 *
 * @Author yxz
 * @Date 2024-12-12 14:51:07
 * @Copyright (c)2024 yxz
 */

@Mapper
@Component
public interface CustomerDao extends BaseMapper<CustomerEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<CustomerVO> queryPage(Page page, @Param("queryForm") CustomerQueryForm queryForm);

    /*
    * 根据客户编码查询客户
    * */
    List<CustomerEntity> queryByCustomerCode(@Param("customerCode") String customerCode);

    List<Long> queryByCustomerName(@Param("customerName") String customerName);

    String getCustomerNameById(@Param("customerId") Long customerId);
}
