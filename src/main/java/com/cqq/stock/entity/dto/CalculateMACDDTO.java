package com.cqq.stock.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 *
 * @author qiqi.chen
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculateMACDDTO {
    @NotNull(message = "code not null")
    private String code;

}
