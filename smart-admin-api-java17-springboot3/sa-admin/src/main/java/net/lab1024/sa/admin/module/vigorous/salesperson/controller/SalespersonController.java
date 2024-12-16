package net.lab1024.sa.admin.module.vigorous.salesperson.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonAddForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonQueryForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.form.SalespersonUpdateForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.vo.SalespersonVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.base.common.domain.ValidateList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

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
}
