package net.lab1024.sa.admin.module.vigorous.sales.order.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.vigorous.sales.order.domain.form.SalesOrderAddForm;
import net.lab1024.sa.admin.module.vigorous.sales.order.domain.form.SalesOrderQueryForm;
import net.lab1024.sa.admin.module.vigorous.sales.order.domain.vo.SalesOrderExcelVO;
import net.lab1024.sa.admin.module.vigorous.sales.order.service.SalesOrderService;
import net.lab1024.sa.admin.module.vigorous.sales.order.domain.form.SalesOrderUpdateForm;
import net.lab1024.sa.admin.module.vigorous.sales.order.domain.vo.SalesOrderVO;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.util.SmartExcelUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 销售订单表 Controller
 *
 * @Author yxz
 * @Date 2025-10-23 14:10:32
 * @Copyright (c)2024 yxz
 */

@RestController
@Tag(name = "销售订单表")
public class SalesOrderController {

    @Resource
    private SalesOrderService salesOrderService;

    @Operation(summary = "分页查询 @author yxz")
    @PostMapping("/salesOrder/queryPage")
    @SaCheckPermission("salesOrder:query")
    public ResponseDTO<PageResult<SalesOrderVO>> queryPage(@RequestBody @Valid SalesOrderQueryForm queryForm) {
        return ResponseDTO.ok(salesOrderService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author yxz")
    @PostMapping("/salesOrder/add")
    @SaCheckPermission("salesOrder:add")
    public ResponseDTO<String> add(@RequestBody @Valid SalesOrderAddForm addForm) {
        return salesOrderService.add(addForm);
    }

    @Operation(summary = "更新 @author yxz")
    @PostMapping("/salesOrder/update")
    @SaCheckPermission("salesOrder:update")
    public ResponseDTO<String> update(@RequestBody @Valid SalesOrderUpdateForm updateForm) {
        return salesOrderService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author yxz")
    @PostMapping("/salesOrder/batchDelete")
    @SaCheckPermission("salesOrder:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return salesOrderService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author yxz")
    @GetMapping("/salesOrder/delete/{salesOrderId}")
    @SaCheckPermission("salesOrder:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long salesOrderId) {
        return salesOrderService.delete(salesOrderId);
    }

    // --------------- 导出和导入 -------------------
    @Operation(summary = "导入")
    @PostMapping("/salesOrder/import")
    @SaCheckPermission("salesOrder:import")
    public ResponseDTO<String> importSalesOrder(@RequestParam MultipartFile file, @RequestParam("mode") Boolean mode ) {
        return salesOrderService.importSalesOrder(file, mode);
    }

    @Operation(summary = "导出")
    @PostMapping("/salesOrder/export")
    @SaCheckPermission("salesOrder:export")
    public void exportSalesOrder(HttpServletResponse response, @RequestBody @Valid SalesOrderQueryForm queryForm) throws IOException {
        List<SalesOrderExcelVO> goodsList = salesOrderService.exportSalesOrder(queryForm);
        SmartExcelUtil.exportExcel(response,"销售订单表.xlsx","销售订单表",SalesOrderExcelVO.class, goodsList);
    }

}
