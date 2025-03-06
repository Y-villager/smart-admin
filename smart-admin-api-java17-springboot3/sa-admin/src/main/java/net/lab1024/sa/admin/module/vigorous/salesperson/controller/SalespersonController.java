package net.lab1024.sa.admin.module.vigorous.salesperson.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.dto.SalespersonDto;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonAddForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonQueryForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonUpdateForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.vo.SalespersonExcelVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.vo.SalespersonVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelRecordAddForm;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.util.SmartExcelUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 业务员 Controller
 *
 * @Author yxz
 * @Date 2024-12-16 10:56:45
 * @Copyright (c)2024 yxz
 */

@RestController
@Tag(name = "业务员")
public class SalespersonController {

    @Resource
    private SalespersonService salespersonService;

    @Operation(summary = "分页查询 @author yxz")
    @PostMapping("/salesperson/queryPage")
    @SaCheckPermission("salesperson:query")
    public ResponseDTO<PageResult<SalespersonVO>> queryPage(@RequestBody @Valid SalespersonQueryForm queryForm) {
        return ResponseDTO.ok(salespersonService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author yxz")
    @PostMapping("/salesperson/add")
    @SaCheckPermission("salesperson:add")
    public ResponseDTO<String> add(@RequestBody @Valid SalespersonAddForm addForm) {
        return salespersonService.add(addForm);
    }

    @Operation(summary = "更新 @author yxz")
    @PostMapping("/salesperson/update")
    @SaCheckPermission("salesperson:update")
    public ResponseDTO<String> update(@RequestBody @Valid SalespersonUpdateForm updateForm) {
        return salespersonService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author yxz")
    @PostMapping("/salesperson/batchDelete")
    @SaCheckPermission("salesperson:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return salespersonService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author yxz")
    @GetMapping("/salesperson/delete/{id}")
    @SaCheckPermission("salesperson:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return salespersonService.delete(id);
    }

    // --------------- 导出和导入 -------------------
    @Operation(summary = "导入")
    @PostMapping("/salesperson/import")
    @SaCheckPermission("salesperson:import")
    public ResponseDTO<String> importSalesperson(@RequestParam MultipartFile file) {
        return salespersonService.importSalesperson(file);
    }

    @Operation(summary = "导出")
    @GetMapping("/salesperson/export")
    @SaCheckPermission("salesperson:export")
    public void exportSalesperson(HttpServletResponse response) throws IOException {
        List<SalespersonExcelVO> goodsList = salespersonService.exportSalespersons();
        SmartExcelUtil.exportExcel(response,"商品列表.xlsx","商品",SalespersonExcelVO.class, goodsList);
    }

    @Operation(summary = "更新 @author yxz")
    @PostMapping("/salesperson/updateLevel")
    public ResponseDTO<String> updateLevel(@RequestBody @Valid SalespersonLevelRecordAddForm updateForm) {
        return salespersonService.updateLevel(updateForm);
    }

    @Operation(summary = "获取所有业务员")
    @GetMapping("/salesperson/getAllSalesperson")
    public ResponseDTO<List<SalespersonDto>> getAllSalesperson() {
        List<SalespersonDto> allSalesperson = salespersonService.getAllSalesperson();
        return ResponseDTO.ok(allSalesperson);
    }

    @Operation(summary = "单个删除 @author yxz")
    @PostMapping("/salesperson/updateDisabled/{id}")
    @SaCheckPermission("salesperson:disabled")
    public ResponseDTO<String> updateDisabled(@PathVariable Long id) {
        return salespersonService.updateDisabledFlag(id);
    }


}
