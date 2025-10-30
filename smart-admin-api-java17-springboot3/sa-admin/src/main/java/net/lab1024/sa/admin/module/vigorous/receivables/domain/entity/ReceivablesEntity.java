package

        net.lab1024.sa.admin.module.vigorous.receivables.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * 应收单 实体类
 *
 * @Author yxz
 * @Date 2024-12-12 14:46:31
 * @Copyright yxz
 */

@Data
@TableName("v_receivables")
public class ReceivablesEntity {

    /**
     * 应收表id
     */
    @TableId(type = IdType.AUTO)
    private Integer receivablesId;

    /**
     * 应收表-单据编号
     */
    private String billNo;

    /**
     * 源单编号
     */
    private String originBillNo;

    /**
     * 应收日期
     */
    private LocalDate receivablesDate;

    /**
     * 业务员编号
     */
    private Long salespersonId;

    /**
     * 客户编号
     */
    private Long customerId;

    /**
     * 单据类型
     */
    private String receivablesType;

    /**
     * 税收合计
     */
    private BigDecimal amount;

    /**
     * 币种
     */
    private String currencyType;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 税收合计（人民币
     */
    private BigDecimal fallAmount;

    /**
     * 付款方
     */
    private String payer;

    /**
     * 应收比例(%)
     */
    private Integer rate;

    private List<ReceivablesDetailsEntity> materials;

    @Override
    public int hashCode() {
        return Objects.hash(billNo); // 基于属性生成哈希值
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReceivablesEntity)) return false;
        return Objects.equals(billNo, ((ReceivablesEntity) o).getBillNo());
    }

}
