package net.lab1024.sa.admin.module.vigorous.receivables.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.entity.ReceivablesEntity;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesQueryForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.vo.ReceivablesVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 应收单 Dao
 *
 * @Author yxz
 * @Date 2024-12-12 14:46:31
 * @Copyright yxz
 */

@Mapper
@Component
public interface ReceivablesDao extends BaseMapper<ReceivablesEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<ReceivablesVO> queryPage(Page page, @Param("queryForm") ReceivablesQueryForm queryForm);


    /**
     * 根据单据编号查询 应收单
     * @param billNos
     * @return
     */
    List<ReceivablesVO> queryByBillNos(@Param("billNos") Set<String> billNos);

    /**
     * 根据 单据编号 批量更新
     * @param entityList
     * @return
     */
    int updateReceivablesByBillNo(@Param("list") List<ReceivablesEntity> entityList);

    /**
     * 批量更新
     * @param entityList
     * @return
     */
    int batchUpdate(@Param("list") List<?> entityList);
}
