package net.lab1024.sa.admin.module.vigorous.receivables.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesAddForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesQueryForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesUpdateForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.vo.ReceivablesVO;
import net.lab1024.sa.admin.module.vigorous.receivables.service.ReceivablesService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.util.SmartExcelUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 应收单 Controller
 *
 * @Author yxz
 * @Date 2024-12-12 14:46:31
 * @Copyright yxz
 */

@RestController
@Tag(name = "应收单")
public class ReceivablesController {

    @Resource
    private ReceivablesService receivablesService;

    @Operation(summary = "分页查询 @author yxz")
    @PostMapping("/receivables/queryPage")
    @SaCheckPermission("receivables:query")
    public ResponseDTO<PageResult<ReceivablesVO>> queryPage(@RequestBody @Valid ReceivablesQueryForm queryForm) {
        return ResponseDTO.ok(receivablesService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author yxz")
    @PostMapping("/receivables/add")
    @SaCheckPermission("receivables:add")
    public ResponseDTO<String> add(@RequestBody @Valid ReceivablesAddForm addForm) {
        return receivablesService.add(addForm);
    }

    @Operation(summary = "更新 @author yxz")
    @PostMapping("/receivables/update")
    @SaCheckPermission("receivables:update")
    public ResponseDTO<String> update(@RequestBody @Valid ReceivablesUpdateForm updateForm) {
        return receivablesService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author yxz")
    @PostMapping("/receivables/batchDelete")
    @SaCheckPermission("receivables:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Integer> idList) {
        return receivablesService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author yxz")
    @GetMapping("/receivables/delete/{receivablesId}")
    @SaCheckPermission("receivables:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Integer receivablesId) {
        return receivablesService.delete(receivablesId);
    }

    // --------------- 导出和导入 -------------------
    @Operation(summary = "导入")
    @PostMapping("/receivables/import")
    @SaCheckPermission("receivables:import")
    public ResponseDTO<String> importReceivables(@RequestParam("file") MultipartFile file, @RequestParam("mode") Boolean mode ) {
        return receivablesService.importReceivables(file, mode);
    }

    @Operation(summary = "导出")
    @GetMapping("/receivables/export")
    @SaCheckPermission("receivables:export")
    public void exportReceivables(HttpServletResponse response) throws IOException {
        List<ReceivablesVO> goodsList = receivablesService.getAllReceivables();
        SmartExcelUtil.exportExcel(response,"应收单.xlsx","应收单",ReceivablesVO.class, goodsList);
    }

}
