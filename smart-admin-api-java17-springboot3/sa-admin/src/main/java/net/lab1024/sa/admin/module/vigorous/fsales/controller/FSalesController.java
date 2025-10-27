package net.lab1024.sa.admin.module.vigorous.fsales.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.vigorous.fsales.domain.form.FSalesAddForm;
import net.lab1024.sa.admin.module.vigorous.fsales.domain.form.FSalesQueryForm;
import net.lab1024.sa.admin.module.vigorous.fsales.domain.form.FSalesUpdateForm;
import net.lab1024.sa.admin.module.vigorous.fsales.domain.vo.FSalesVO;
import net.lab1024.sa.admin.module.vigorous.fsales.service.FSalesService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.util.SmartExcelUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 发货通知单 Controller
 *
 * @Author yxz
 * @Date 2025-10-23 14:11:35
 * @Copyright (c)2024 yxz
 */

@RestController
@Tag(name = "发货通知单")
public class FSalesController {

    @Resource
    private FSalesService fSalesService;

    @Operation(summary = "分页查询 @author yxz")
    @PostMapping("/fSales/queryPage")
    @SaCheckPermission("fSales:query")
    public ResponseDTO<PageResult<FSalesVO>> queryPage(@RequestBody @Valid FSalesQueryForm queryForm) {
        return ResponseDTO.ok(fSalesService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author yxz")
    @PostMapping("/fSales/add")
    @SaCheckPermission("fSales:add")
    public ResponseDTO<String> add(@RequestBody @Valid FSalesAddForm addForm) {
        return fSalesService.add(addForm);
    }

    @Operation(summary = "更新 @author yxz")
    @PostMapping("/fSales/update")
    @SaCheckPermission("fSales:update")
    public ResponseDTO<String> update(@RequestBody @Valid FSalesUpdateForm updateForm) {
        return fSalesService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author yxz")
    @PostMapping("/fSales/batchDelete")
    @SaCheckPermission("fSales:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return fSalesService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author yxz")
    @GetMapping("/fSales/delete/{fSalesId}")
    @SaCheckPermission("fSales:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long fSalesId) {
        return fSalesService.delete(fSalesId);
    }

    // --------------- 导出和导入 -------------------
    @Operation(summary = "导入")
    @PostMapping("/fSales/import")
    @SaCheckPermission("fSales:import")
    public ResponseDTO<String> importFSales(@RequestParam MultipartFile file, @RequestParam("mode") Boolean mode ) {
        return fSalesService.importFSales(file, mode);
    }

    @Operation(summary = "导出")
    @PostMapping("/fSales/export")
    @SaCheckPermission("fSales:export")
    public void exportFSales(HttpServletResponse response, @RequestBody @Valid FSalesQueryForm queryForm) throws IOException {
        List<FSalesVO> goodsList = fSalesService.exportFSales(queryForm);
        SmartExcelUtil.exportExcel(response,"发货通知单.xlsx","发货通知单",FSalesVO.class, goodsList);
    }

}
