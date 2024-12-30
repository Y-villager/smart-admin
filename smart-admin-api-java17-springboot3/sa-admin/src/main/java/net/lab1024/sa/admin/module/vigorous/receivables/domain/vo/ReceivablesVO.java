package

        net.lab1024.sa.admin.module.vigorous.receivables.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.json.serializer.DictValueVoSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 应收单 列表VO
 *
 * @Author yxz
 * @Date 2024-12-12 14:46:31
 * @Copyright yxz
 */

@Data
public class ReceivablesVO {


    @Schema(description = "应收表id")
    private Integer receivablesId;

    @Schema(description = "应收表-单据编号")
    private String billNo;

    @Schema(description = "业务日期")
    private LocalDate receivablesDate;

    @Schema(description = "业务员编号")
    private String salespersonId;

    @Schema(description = "客户编号")
    private String customerId;

    @Schema(description = "源单编号")
    private String originBillNo;

    @Schema(description = "税收合计")
    private BigDecimal amount;

    @Schema(description = "币种")
    @JsonSerialize(using = DictValueVoSerializer.class)
    private String currencyType;

    @Schema(description = "应收比例(%)")
    private Integer rate;


}
