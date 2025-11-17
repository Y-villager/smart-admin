package net.lab1024.sa.admin.module.vigorous.receivables.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesDetailsAddForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesDetailsQueryForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesDetailsUpdateForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.vo.ReceivablesDetailsVO;
import net.lab1024.sa.admin.module.vigorous.receivables.service.ReceivablesDetailsService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.util.SmartExcelUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 应收明细表 Controller
 *
 * @Author yxz
 * @Date 2025-10-23 14:43:51
 * @Copyright (c)2025 yxz
 */

@RestController
@Tag(name = "应收明细表")
public class ReceivablesDetailsController {

    @Resource
    private ReceivablesDetailsService receivablesDetailsService;

    @Operation(summary = "分页查询 @author yxz")
    @PostMapping("/receivablesDetails/queryPage")
    @SaCheckPermission("receivablesDetails:query")
    public ResponseDTO<PageResult<ReceivablesDetailsVO>> queryPage(@RequestBody @Valid ReceivablesDetailsQueryForm queryForm) {
        return ResponseDTO.ok(receivablesDetailsService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author yxz")
    @PostMapping("/receivablesDetails/add")
    @SaCheckPermission("receivablesDetails:add")
    public ResponseDTO<String> add(@RequestBody @Valid ReceivablesDetailsAddForm addForm) {
        return receivablesDetailsService.add(addForm);
    }

    @Operation(summary = "更新 @author yxz")
    @PostMapping("/receivablesDetails/update")
    @SaCheckPermission("receivablesDetails:update")
    public ResponseDTO<String> update(@RequestBody @Valid ReceivablesDetailsUpdateForm updateForm) {
        return receivablesDetailsService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author yxz")
    @PostMapping("/receivablesDetails/batchDelete")
    @SaCheckPermission("receivablesDetails:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Integer> idList) {
        return receivablesDetailsService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author yxz")
    @GetMapping("/receivablesDetails/delete/{id}")
    @SaCheckPermission("receivablesDetails:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Integer id) {
        return receivablesDetailsService.delete(id);
    }

    // --------------- 导出和导入 -------------------
    @Operation(summary = "导入")
    @PostMapping("/receivablesDetails/import")
    @SaCheckPermission("receivablesDetails:import")
    public ResponseDTO<String> importReceivablesDetails(@RequestParam MultipartFile file, @RequestParam("mode") Boolean mode ) {
        return receivablesDetailsService.importReceivablesDetails(file, mode);
    }

    @Operation(summary = "导出")
    @PostMapping("/receivablesDetails/export")
    @SaCheckPermission("receivablesDetails:export")
    public void exportReceivablesDetails(HttpServletResponse response, @RequestBody @Valid ReceivablesDetailsQueryForm queryForm) throws IOException {
        List<ReceivablesDetailsVO> goodsList = receivablesDetailsService.exportReceivablesDetails(queryForm);
        SmartExcelUtil.exportExcel(response,"应收明细表.xlsx","应收明细表",ReceivablesDetailsVO.class, goodsList);
    }

}
