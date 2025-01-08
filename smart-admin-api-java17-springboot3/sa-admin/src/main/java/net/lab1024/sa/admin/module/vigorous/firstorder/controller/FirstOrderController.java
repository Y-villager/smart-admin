package net.lab1024.sa.admin.module.vigorous.firstorder.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.vigorous.customer.domain.vo.CustomerVO;
import net.lab1024.sa.admin.module.vigorous.customer.service.CustomerService;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.entity.FirstOrderEntity;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.form.FirstOrderAddForm;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.form.FirstOrderQueryForm;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.form.FirstOrderUpdateForm;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.result.InsertResult;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.vo.FirstOrderExcelVO;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.vo.FirstOrderVO;
import net.lab1024.sa.admin.module.vigorous.firstorder.service.FirstOrderService;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo.SalesOutboundVO;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.service.SalesOutboundService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.util.SmartExcelUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
    @Autowired
    private CustomerService customerService;
    @Autowired
    private SalesOutboundService salesOutboundService;

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

    @Operation(summary = "导入")
    @PostMapping("/firstOrder/import")
    @SaCheckPermission("firstOrder:import")
    public ResponseDTO<String> importFirstOrder(@RequestParam MultipartFile file, @Param("mode") Boolean mode) {
        return firstOrderService.importFirstOrder(file, mode);
    }

    // --------------- 导出 -------------------
    @Operation(summary = "导出")
    @GetMapping("/firstOrder/export")
    @SaCheckPermission("firstOrder:export")
    public void exportFirstOrder(HttpServletResponse response) throws IOException {
        List<FirstOrderExcelVO> goodsList = firstOrderService.getAllFirstOrder();
        SmartExcelUtil.exportExcel(response,"客户首单信息.xlsx","客户首单信息",FirstOrderExcelVO.class, goodsList);
    }


    @Operation(summary = "初始化客户首单信息")
//    @PostMapping("/firstOrder/init")
    public ResponseDTO<String> initCustomerFirstOrder(HttpServletResponse response) throws IOException {
        // 查询首单为空的客户
//        LambdaQueryWrapper<CustomerEntity> queryWrapper = new LambdaQueryWrapper<>();
        List<CustomerVO> list = customerService.getCustomerOfFONull();

        List<FirstOrderEntity> initList = new ArrayList<>();
        List<Long> customerIdToUpdate = new ArrayList<>();

        for (CustomerVO e : list) {
            // 首单
            FirstOrderEntity firstOrder = new FirstOrderEntity();
            SalesOutboundVO salesOutboundVO = salesOutboundService.queryFirstOrderOfCustomer(e.getCustomerId());

            if (salesOutboundVO == null) {
                continue;
            }
            // 初始话首单信息
            firstOrder.setCustomerId(e.getCustomerId());
            firstOrder.setBillNo(salesOutboundVO.getBillNo());
            firstOrder.setSalespersonId(salesOutboundVO.getSalespersonId());
            firstOrder.setOrderDate(salesOutboundVO.getSalesBoundDate());
            // 要插入的首单记录
            initList.add(firstOrder);
            // 要更新的用户
            customerIdToUpdate.add(e.getCustomerId());
        }

        if (!initList.isEmpty()) {
            firstOrderService.insertBatchFirstOrder(initList);
        }else {
            return ResponseDTO.ok("所有客户的首单已关联");
        }

        // 获取插入后的首单主键并更新客户表（批量更新）
        if (!initList.isEmpty()) {
            // 批量更新客户的 first_order_id 字段
            InsertResult insertResult = customerService.updateFirstOrderIds(customerIdToUpdate, initList);
            return ResponseDTO.okMsg(insertResult.getMessage());
        }
        return ResponseDTO.ok();

    }

}
