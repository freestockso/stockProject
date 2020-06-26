package com.cqq.stock.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculateMACDDTO {
    @NotNull(message = "code not null")
    private String code;

}
