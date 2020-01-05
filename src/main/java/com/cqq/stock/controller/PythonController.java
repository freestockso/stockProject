package com.cqq.stock.controller;

import com.cqq.stock.entity.dto.CallDTO;
import com.cqq.stock.service.PythonService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("python")
@AllArgsConstructor
public class PythonController {
    private PythonService pythonService;


    @PostMapping("call")
    public String call(@RequestBody @Valid CallDTO callDTO) throws IOException, InterruptedException {
        pythonService.call(callDTO.getDate());
        return "success";

    }


}
