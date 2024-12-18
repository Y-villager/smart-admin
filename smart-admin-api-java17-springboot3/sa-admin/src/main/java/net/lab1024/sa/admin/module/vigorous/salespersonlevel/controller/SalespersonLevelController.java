package net.lab1024.sa.admin.module.vigorous.salespersonlevel.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelAddForm;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelQueryForm;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form.SalespersonLevelUpdateForm;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.vo.SalespersonLevelExcelVO;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.vo.SalespersonLevelVO;
import net.lab1024.sa.admin.module.vigorous.salespersonlevel.service.SalespersonLevelService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.util.SmartExcelUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 业务员级别 Controller
 *
 * @Author yxz
 * @Date 2024-12-14 16:07:14
 * @Copyright (c)2024 yxz
 */

@RestController
@Tag(name = "业务员级别")
public class SalespersonLevelController {

    @Resource
    private SalespersonLevelService salespersonLevelService;

    @Operation(summary = "分页查询 @author yxz")
    @PostMapping("/salespersonLevel/queryPage")
    @SaCheckPermission("salespersonLevel:query")
    public ResponseDTO<PageResult<SalespersonLevelVO>> queryPage(@RequestBody @Valid SalespersonLevelQueryForm queryForm) {
        return ResponseDTO.ok(salespersonLevelService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author yxz")
    @PostMapping("/salespersonLevel/add")
    @SaCheckPermission("salespersonLevel:add")
    public ResponseDTO<String> add(@RequestBody @Valid SalespersonLevelAddForm addForm) {
        return salespersonLevelService.add(addForm);
    }

    @Operation(summary = "更新 @author yxz")
    @PostMapping("/salespersonLevel/update")
    @SaCheckPermission("salespersonLevel:update")
    public ResponseDTO<String> update(@RequestBody @Valid SalespersonLevelUpdateForm updateForm) {
        return salespersonLevelService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author yxz")
    @PostMapping("/salespersonLevel/batchDelete")
    @SaCheckPermission("salespersonLevel:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Integer> idList) {
        return salespersonLevelService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author yxz")
    @GetMapping("/salespersonLevel/delete/{salespersonLevelId}")
    @SaCheckPermission("salespersonLevel:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Integer salespersonLevelId) {
        return salespersonLevelService.delete(salespersonLevelId);
    }

    // --------------- 导出和导入 -------------------
    @Operation(summary = "导入")
    @PostMapping("/salespersonLevel/import")
    @SaCheckPermission("salespersonLevel:import")
    public ResponseDTO<String> importSalespersonLevel(@RequestParam MultipartFile file) {
        return salespersonLevelService.importSalespersonLevel(file);
    }

    @Operation(summary = "导出")
    @GetMapping("/salespersonLevel/export")
    @SaCheckPermission("salespersonLevel:export")
    public void exportSalespersonLevel(HttpServletResponse response) throws IOException {
        List<SalespersonLevelExcelVO> goodsList = salespersonLevelService.getAllSalespersonLevel();
        SmartExcelUtil.exportExcel(response,"业务员级别.xlsx","业务员级别",SalespersonLevelExcelVO.class, goodsList);
    }

}
