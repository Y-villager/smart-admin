package net.lab1024.sa.admin.module.vigorous.commission.calc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.form.CommissionRecordAddForm;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.form.CommissionRecordQueryForm;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.form.CommissionRecordUpdateForm;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.vo.CommissionRecordVO;
import net.lab1024.sa.admin.module.vigorous.commission.calc.service.CommissionRecordService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.util.SmartExcelUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * 业务提成记录 Controller
 *
 * @Author yxz
 * @Date 2025-01-12 15:25:35
 * @Copyright (c)2024 yxz
 */

@RestController
@Tag(name = "业务提成记录")
public class CommissionRecordController {

    @Resource
    private CommissionRecordService commissionRecordService;

    @Operation(summary = "分页查询 @author yxz")
    @PostMapping("/commissionRecord/queryPage")
    @SaCheckPermission("commissionRecord:query")
    public ResponseDTO<PageResult<CommissionRecordVO>> queryPage(@RequestBody @Valid CommissionRecordQueryForm queryForm) {
        return ResponseDTO.ok(commissionRecordService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author yxz")
    @PostMapping("/commissionRecord/add")
    @SaCheckPermission("commissionRecord:add")
    public ResponseDTO<String> add(@RequestBody @Valid CommissionRecordAddForm addForm) {
        return commissionRecordService.add(addForm);
    }

    @Operation(summary = "更新 @author yxz")
    @PostMapping("/commissionRecord/update")
    @SaCheckPermission("commissionRecord:update")
    public ResponseDTO<String> update(@RequestBody @Valid CommissionRecordUpdateForm updateForm) {
        return commissionRecordService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author yxz")
    @PostMapping("/commissionRecord/batchDelete")
    @SaCheckPermission("commissionRecord:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return commissionRecordService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author yxz")
    @GetMapping("/commissionRecord/delete/{commissionId}")
    @SaCheckPermission("commissionRecord:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long commissionId) {
        return commissionRecordService.delete(commissionId);
    }

    // --------------- 导出和导入 -------------------
    @Operation(summary = "导入")
    @PostMapping("/commissionRecord/import")
    @SaCheckPermission("commissionRecord:import")
    public ResponseDTO<String> importCommissionRecord(@RequestParam MultipartFile file, @RequestParam("mode") Boolean mode ) {
        return commissionRecordService.importCommissionRecord(file, mode);
    }

    @Operation(summary = "按提成类别导出")
    @PostMapping("/commissionRecord/export")
    @SaCheckPermission("commissionRecord:export")
    public void exportCommissionRecord(HttpServletResponse response, @RequestBody @Valid CommissionRecordQueryForm queryForm) throws IOException {
        Map<String, Collection<?>> resList = commissionRecordService.exportCommissionRecord(queryForm);
        SmartExcelUtil.exportExcel(response,"业务提成.xlsx", resList);
    }

    @Operation(summary = "按业务员导出")
    @PostMapping("/commissionRecord/export2")
    @SaCheckPermission("commissionRecord:export")
    public void exportCommissionRecord2(HttpServletResponse response, @RequestBody @Valid CommissionRecordQueryForm queryForm) throws IOException {
        Map<String, Collection<?>> resList = commissionRecordService.exportCommissionRecordBySalesperson(queryForm);
        SmartExcelUtil.exportExcel(response,"业务提成.xlsx", resList);
    }

}
