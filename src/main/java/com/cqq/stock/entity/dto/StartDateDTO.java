package com.cqq.stock.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StartDateDTO {
    @NotNull(message = "date不能为空")
    private Long date;
}
