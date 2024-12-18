package net.lab1024.sa.admin.module.vigorous.firstorder.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.form.FirstOrderAddForm;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.form.FirstOrderQueryForm;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.form.FirstOrderUpdateForm;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.vo.FirstOrderVO;
import net.lab1024.sa.admin.module.vigorous.firstorder.service.FirstOrderService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.util.SmartExcelUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 客户首单信息 Controller
 *
 * @Author yxz
 * @Date 2024-12-12 14:50:26
 * @Copyright (c)2024 yxz
 */

@RestController
@Tag(name = "客户首单信息")
public class FirstOrderController {

    @Resource
    private FirstOrderService firstOrderService;

    @Operation(summary = "分页查询 @author yxz")
    @PostMapping("/firstOrder/queryPage")
    @SaCheckPermission("firstOrder:query")
    public ResponseDTO<PageResult<FirstOrderVO>> queryPage(@RequestBody @Valid FirstOrderQueryForm queryForm) {
        return ResponseDTO.ok(firstOrderService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author yxz")
    @PostMapping("/firstOrder/add")
    @SaCheckPermission("firstOrder:add")
    public ResponseDTO<String> add(@RequestBody @Valid FirstOrderAddForm addForm) {
        return firstOrderService.add(addForm);
    }

    @Operation(summary = "更新 @author yxz")
    @PostMapping("/firstOrder/update")
    @SaCheckPermission("firstOrder:update")
    public ResponseDTO<String> update(@RequestBody @Valid FirstOrderUpdateForm updateForm) {
        return firstOrderService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author yxz")
    @PostMapping("/firstOrder/batchDelete")
    @SaCheckPermission("firstOrder:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return firstOrderService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author yxz")
    @GetMapping("/firstOrder/delete/{firstOrderId}")
    @SaCheckPermission("firstOrder:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long firstOrderId) {
        return firstOrderService.delete(firstOrderId);
    }

    // --------------- 导出和导入 -------------------
    @Operation(summary = "导入")
    @PostMapping("/firstOrder/import")
    @SaCheckPermission("firstOrder:import")
    public ResponseDTO<String> importFirstOrder(@RequestParam MultipartFile file) {
        return firstOrderService.importFirstOrder(file);
    }

    @Operation(summary = "导出")
    @GetMapping("/firstOrder/export")
    @SaCheckPermission("firstOrder:export")
    public void exportFirstOrder(HttpServletResponse response) throws IOException {
        List<FirstOrderVO> goodsList = firstOrderService.getAllFirstOrder();
        SmartExcelUtil.exportExcel(response,"客户首单信息.xlsx","客户首单信息",FirstOrderVO.class, goodsList);
    }

}
