package com.cqq.stock.controller;

import com.cqq.stock.entity.dto.CalculateMACDDTO;
import com.cqq.stock.service.MacdService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("macd")
public class MacdController {

    private MacdService macdService;
    @PostMapping("calculateMACD")
    public void  calculateMACD(@RequestBody @Valid CalculateMACDDTO calculateMACDDTO){
        macdService.calculateMACD(calculateMACDDTO);


    }
}
