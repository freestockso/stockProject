package com.cqq.stock.controller;

import com.cqq.stock.timer.TuShareSynchronizedDataTimer;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("sync")
public class SyncController {

    private TuShareSynchronizedDataTimer tuShareSynchronizedDataTimer;

    @PostMapping("syncDataFromNetworkToDesk")
    public void syncDataFromNetworkToDesk() {
        tuShareSynchronizedDataTimer.syncDataFromNetworkToDesk();
    }

    @PostMapping("desk2DB")
    public void desk2DB() {
        tuShareSynchronizedDataTimer.desk2DB();
    }

    @PostMapping("dbCalculate")
    public void dbCalculate() throws InterruptedException {
        tuShareSynchronizedDataTimer.dbCalculate();

    }

    @PostMapping("changeError")
    public void changeError() {
        tuShareSynchronizedDataTimer.changeError();
    }

    @PostMapping("test")
    public void test() {
        tuShareSynchronizedDataTimer.test();
    }

    @PostMapping("syncDataFromNetworkToDeskQuickly")
    public void syncDataFromNetworkToDeskQuickly() throws InterruptedException {
        tuShareSynchronizedDataTimer.syncDataFromNetworkToDeskQuickly();
    }
}
