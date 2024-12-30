package net.lab1024.sa.admin.module.vigorous.commission.calc;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.SalesOutboundQueryForm;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo.SalesOutboundVO;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.service.SalesOutboundService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.*;

/**
 *
 */
@RestController
@Tag(name = "业务提成计算")
@RequestMapping("/commission/calc")
public class CommissionCalcController {

    @Resource
    private SalesOutboundService salesOutboundService;

    @PostMapping("/queryPage")
    public ResponseDTO<PageResult<SalesOutboundVO>> queryPage(@RequestBody @Valid SalesOutboundQueryForm queryForm){
        return ResponseDTO.ok(salesOutboundService.queryPageWithReceivables(queryForm));
    }
}
