package com.cqq.stock.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DateDTO {
    @NotNull(message = "date not null")
    private Long date;


}
