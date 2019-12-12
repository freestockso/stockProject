package com.cqq.stock.controller;

import com.cqq.stock.entity.ListEntity;
import com.cqq.stock.entity.StockInfo;
import com.cqq.stock.service.NoticeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class NoticeController {

    private NoticeService noticeService;

    @GetMapping("noticeToBuy")
    public ListEntity<StockInfo> noticeToBuy(String code) {
        return noticeService.noticeToBuy(code);
    }

    @GetMapping("noticeToSale")
    public ListEntity<StockInfo> noticeToSale(String code) {
        return noticeService.noticeToSale(code);
    }
}
