package net.lab1024.sa.admin.module.vigorous.customer.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.vigorous.customer.domain.form.CustomerAddForm;
import net.lab1024.sa.admin.module.vigorous.customer.domain.form.CustomerQueryForm;
import net.lab1024.sa.admin.module.vigorous.customer.domain.form.CustomerUpdateForm;
import net.lab1024.sa.admin.module.vigorous.customer.domain.vo.CustomerExcelVO;
import net.lab1024.sa.admin.module.vigorous.customer.domain.vo.CustomerVO;
import net.lab1024.sa.admin.module.vigorous.customer.service.CustomerService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.util.SmartExcelUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 顾客 Controller
 *
 * @Author yxz
 * @Date 2024-12-12 14:51:07
 * @Copyright (c)2024 yxz
 */

@RestController
@Tag(name = "顾客")
public class CustomerController {

    @Resource
    private CustomerService customerService;

    @Operation(summary = "分页查询 @author yxz")
    @PostMapping("/customer/queryPage")
    @SaCheckPermission("customer:query")
    public ResponseDTO<PageResult<CustomerVO>> queryPage(@RequestBody @Valid CustomerQueryForm queryForm) {
        return ResponseDTO.ok(customerService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author yxz")
    @PostMapping("/customer/add")
    @SaCheckPermission("customer:add")
    public ResponseDTO<String> add(@RequestBody @Valid CustomerAddForm addForm) {
        return customerService.add(addForm);
    }

    @Operation(summary = "更新 @author yxz")
    @PostMapping("/customer/update")
    @SaCheckPermission("customer:update")
    public ResponseDTO<String> update(@RequestBody @Valid CustomerUpdateForm updateForm) {
        return customerService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author yxz")
    @PostMapping("/customer/batchDelete")
    @SaCheckPermission("customer:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return customerService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author yxz")
    @GetMapping("/customer/delete/{customerId}")
    @SaCheckPermission("customer:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long customerId) {
        return customerService.delete(customerId);
    }

    // --------------- 导出和导入 -------------------
    @Operation(summary = "导入")
    @PostMapping("/customer/import")
    @SaCheckPermission("customer:import")
    public ResponseDTO<String> importCustomer(@RequestParam MultipartFile file) {
        return customerService.importCustomer(file);
    }

    @Operation(summary = "导出")
    @GetMapping("/customer/export")
    @SaCheckPermission("customer:export")
    public void exportCustomer(HttpServletResponse response) throws IOException {
        List<CustomerExcelVO> goodsList = customerService.getAllCustomer();
        SmartExcelUtil.exportExcel(response,"顾客.xlsx","顾客",CustomerExcelVO.class, goodsList);
    }

}
