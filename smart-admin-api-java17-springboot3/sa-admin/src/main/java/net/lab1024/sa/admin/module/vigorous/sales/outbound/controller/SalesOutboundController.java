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
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo.SalesOutboundVO;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.service.SalesOutboundService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.util.SmartExcelUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 销售出库 Controller
 *
 * @Author yxz
 * @Date 2024-12-12 14:48:19
 * @Copyright (c)2024 yxz
 */

@RestController
@Tag(name = "销售出库")
public class SalesOutboundController {

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
    public ResponseDTO<String> importSalesOutbound(@RequestParam MultipartFile file) {
        return salesOutboundService.importSalesOutbound(file);
    }

    @Operation(summary = "导出")
    @GetMapping("/salesOutbound/export")
    @SaCheckPermission("salesOutbound:export")
    public void exportSalesOutbound(HttpServletResponse response) throws IOException {
        List<SalesOutboundVO> goodsList = salesOutboundService.getAllSalesOutbound();
        SmartExcelUtil.exportExcel(response,"销售出库.xlsx","销售出库",SalesOutboundVO.class, goodsList);
    }

}
