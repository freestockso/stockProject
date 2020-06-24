package com.cqq.stock.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetResultDTO {

    @NotNull(message = "date not null")
    private Long date;

    @NotNull(message = "code not null")
    private String code;


}
