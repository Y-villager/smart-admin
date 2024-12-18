package net.lab1024.sa.admin.module.vigorous.firstorder.dao;

import java.util.List;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.entity.FirstOrderEntity;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.form.FirstOrderQueryForm;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.vo.FirstOrderVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 客户首单信息 Dao
 *
 * @Author yxz
 * @Date 2024-12-12 14:50:26
 * @Copyright (c)2024 yxz
 */

@Mapper
@Component
public interface FirstOrderDao extends BaseMapper<FirstOrderEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<FirstOrderVO> queryPage(Page page, @Param("queryForm") FirstOrderQueryForm queryForm);


}
