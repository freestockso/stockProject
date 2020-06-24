package com.cqq.stock.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class StatusDTO {
    @NotNull(message = "code is not null")
    private List<String> code;
    @NotNull(message = "date is not null")
    private Integer date;
}
