package net.lab1024.sa.admin.module.vigorous.salesperson.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalespersonDto {
    @Schema(description = "主键")
    private Long id;

    private String name;

    private String level;

    private String department;


}
