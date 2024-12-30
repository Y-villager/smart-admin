package net.lab1024.sa.admin.module.vigorous.commission.rule.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.form.CommissionRuleAddForm;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.form.CommissionRuleQueryForm;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.form.CommissionRuleUpdateForm;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.vo.CommissionRuleVO;
import net.lab1024.sa.admin.module.vigorous.commission.rule.service.CommissionRuleService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.util.SmartExcelUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 提成规则 Controller
 *
 * @Author yxz
 * @Date 2024-12-23 16:14:00
 * @Copyright (c)2024 yxz
 */

@RestController
@Tag(name = "提成规则")
public class CommissionRuleController {

    @Resource
    private CommissionRuleService commissionRuleService;

    @Operation(summary = "分页查询 @author yxz")
    @PostMapping("/commissionRule/queryPage")
    @SaCheckPermission("commissionRule:query")
    public ResponseDTO<PageResult<CommissionRuleVO>> queryPage(@RequestBody @Valid CommissionRuleQueryForm queryForm) {
        return ResponseDTO.ok(commissionRuleService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author yxz")
    @PostMapping("/commissionRule/add")
    @SaCheckPermission("commissionRule:add")
    public ResponseDTO<String> add(@RequestBody @Valid CommissionRuleAddForm addForm) {
        return commissionRuleService.add(addForm);
    }

    @Operation(summary = "更新 @author yxz")
    @PostMapping("/commissionRule/update")
    @SaCheckPermission("commissionRule:update")
    public ResponseDTO<String> update(@RequestBody @Valid CommissionRuleUpdateForm updateForm) {
        return commissionRuleService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author yxz")
    @PostMapping("/commissionRule/batchDelete")
    @SaCheckPermission("commissionRule:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return commissionRuleService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author yxz")
    @GetMapping("/commissionRule/delete/{ruleId}")
    @SaCheckPermission("commissionRule:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long ruleId) {
        return commissionRuleService.delete(ruleId);
    }

    // --------------- 导出和导入 -------------------
    @Operation(summary = "导入")
    @PostMapping("/commissionRule/import")
    @SaCheckPermission("commissionRule:import")
    public ResponseDTO<String> importCommissionRule(@RequestParam MultipartFile file) {
        return commissionRuleService.importCommissionRule(file);
    }

    @Operation(summary = "导出")
    @GetMapping("/commissionRule/export")
    @SaCheckPermission("commissionRule:export")
    public void exportCommissionRule(HttpServletResponse response) throws IOException {
        List<CommissionRuleVO> goodsList = commissionRuleService.getAllCommissionRule();
        SmartExcelUtil.exportExcel(response,"提成规则.xlsx","提成规则",CommissionRuleVO.class, goodsList);
    }

}
