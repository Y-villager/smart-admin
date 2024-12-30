package net.lab1024.sa.admin.module.vigorous.commission.rule.dao;

import java.util.List;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.entity.CommissionRuleEntity;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.form.CommissionRuleQueryForm;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.vo.CommissionRuleVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 提成规则 Dao
 *
 * @Author yxz
 * @Date 2024-12-23 16:14:00
 * @Copyright (c)2024 yxz
 */

@Mapper
@Component
public interface CommissionRuleDao extends BaseMapper<CommissionRuleEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<CommissionRuleVO> queryPage(Page page, @Param("queryForm") CommissionRuleQueryForm queryForm);


}
