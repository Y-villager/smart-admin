package net.lab1024.sa.admin.module.vigorous.sales.outbound.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.SalesOutboundAddForm;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.SalesOutboundQueryForm;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.SalesOutboundUpdateForm;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo.SalesOutboundExcelVO;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo.SalesOutboundVO;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.service.SalesOutboundService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.util.SmartExcelUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 销售出库 Controller
 *
 * @Author yxz
 * @Date 2024-12-12 14:48:19
 * @Copyright (c)2024 yxz
 */

@RestController
@Tag(name = "销售出库")
class SalesOutboundController {

    @Resource
    private SalesOutboundService salesOutboundService;

    @Operation(summary = "分页查询 @author yxz")
    @PostMapping("/salesOutbound/queryPage")
    @SaCheckPermission("salesOutbound:query")
    public ResponseDTO<PageResult<SalesOutboundVO>> queryPage(@RequestBody @Valid SalesOutboundQueryForm queryForm) {
        return ResponseDTO.ok(salesOutboundService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author yxz")
    @PostMapping("/salesOutbound/add")
    @SaCheckPermission("salesOutbound:add")
    public ResponseDTO<String> add(@RequestBody @Valid SalesOutboundAddForm addForm) {
        return salesOutboundService.add(addForm);
    }

    @Operation(summary = "更新 @author yxz")
    @PostMapping("/salesOutbound/update")
    @SaCheckPermission("salesOutbound:update")
    public ResponseDTO<String> update(@RequestBody @Valid SalesOutboundUpdateForm updateForm) {
        return salesOutboundService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author yxz")
    @PostMapping("/salesOutbound/batchDelete")
    @SaCheckPermission("salesOutbound:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return salesOutboundService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author yxz")
    @GetMapping("/salesOutbound/delete/{salesBoundId}")
    @SaCheckPermission("salesOutbound:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long salesBoundId) {
        return salesOutboundService.delete(salesBoundId);
    }

    // --------------- 导出和导入 -------------------
    @Operation(summary = "导入")
    @PostMapping("/salesOutbound/import")
    @SaCheckPermission("salesOutbound:import")
    public ResponseDTO<String> importSalesOutbound(@RequestParam("file") MultipartFile file, @RequestParam("mode") Boolean mode) throws IOException {
        return salesOutboundService.importSalesOutbound(file, mode);
    }

    @Operation(summary = "导出")
    @PostMapping("/salesOutbound/export")
    @SaCheckPermission("salesOutbound:export")
    public void exportSalesOutbound(HttpServletResponse response, @RequestBody @Valid SalesOutboundQueryForm queryForm) throws IOException {
        List<SalesOutboundExcelVO> list = salesOutboundService.getExportList(queryForm);
        SmartExcelUtil.exportExcel(response,"销售出库.xlsx","销售出库", SalesOutboundExcelVO.class, list);
    }

    @Operation(summary = "生成所有业绩提成")
    @PostMapping("/salesOutbound/createAllCommission")
    public ResponseDTO<String>  exportAllCommission(HttpServletResponse response, @RequestBody @Valid SalesOutboundQueryForm queryForm) throws IOException {
        return salesOutboundService.createCommission(queryForm);
    }

    @Operation(summary = "生成选中业绩提成")
    @PostMapping("/salesOutbound/createSelectedCommission")
    public ResponseDTO<String>  exportSelectedCommission(@RequestBody ValidateList<Long> idList) throws IOException {
        return salesOutboundService.createSelectedCommission(idList);
    }

    @Operation(summary = "调整生成标识-可覆盖")
    @PostMapping("/salesOutbound/batchUpdateCommissionFlag")
    public ResponseDTO<String>  batchUpdateCommissionFlag(@RequestBody ValidateList<Long> idList) throws IOException {
        // 转换为 Set<Long>
        Set<Long> idSet = new HashSet<>(idList);
        return salesOutboundService.batchUpdateCommissionFlag(idSet);
    }

}
