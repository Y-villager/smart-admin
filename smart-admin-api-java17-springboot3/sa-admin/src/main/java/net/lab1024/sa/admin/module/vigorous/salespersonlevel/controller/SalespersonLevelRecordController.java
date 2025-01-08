package net.lab1024.sa.admin.module.vigorous.salespersonlevel.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelRecordAddForm;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelRecordQueryForm;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelRecordUpdateForm;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.vo.SalespersonLevelRecordVO;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.service.SalespersonLevelRecordService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartExcelUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 业务员级别变动记录 Controller
 *
 * @Author yxz
 * @Date 2025-01-07 08:58:41
 * @Copyright (c)2024 yxz
 */

@RestController
@Tag(name = "业务员级别变动记录")
public class SalespersonLevelRecordController {

    @Resource
    private SalespersonLevelRecordService salespersonLevelRecordService;

    @Operation(summary = "分页查询 @author yxz")
    @PostMapping("/salespersonLevelRecord/queryPage")
    @SaCheckPermission("salespersonLevelRecord:query")
    public ResponseDTO<PageResult<SalespersonLevelRecordVO>> queryPage(@RequestBody @Valid SalespersonLevelRecordQueryForm queryForm) {
        return ResponseDTO.ok(salespersonLevelRecordService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author yxz")
    @PostMapping("/salespersonLevelRecord/add")
    @SaCheckPermission("salespersonLevelRecord:add")
    public ResponseDTO<String> add(@RequestBody @Valid SalespersonLevelRecordAddForm addForm) {
        return salespersonLevelRecordService.add(addForm);
    }

    @Operation(summary = "更新 @author yxz")
    @PostMapping("/salespersonLevelRecord/update")
    @SaCheckPermission("salespersonLevelRecord:update")
    public ResponseDTO<String> update(@RequestBody @Valid SalespersonLevelRecordUpdateForm updateForm) {
        return salespersonLevelRecordService.update(updateForm);
    }


    // --------------- 导出和导入 -------------------
    @Operation(summary = "导入")
    @PostMapping("/salespersonLevelRecord/import")
    @SaCheckPermission("salespersonLevelRecord:import")
    public ResponseDTO<String> importSalespersonLevelRecord(@RequestParam MultipartFile file) {
        return salespersonLevelRecordService.importSalespersonLevelRecord(file);
    }

    @Operation(summary = "导出")
    @GetMapping("/salespersonLevelRecord/export")
    @SaCheckPermission("salespersonLevelRecord:export")
    public void exportSalespersonLevelRecord(HttpServletResponse response) throws IOException {
        List<SalespersonLevelRecordVO> goodsList = salespersonLevelRecordService.getAllSalespersonLevelRecord();
        SmartExcelUtil.exportExcel(response,"业务员级别变动记录.xlsx","业务员级别变动记录",SalespersonLevelRecordVO.class, goodsList);
    }

}
