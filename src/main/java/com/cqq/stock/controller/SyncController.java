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

    @PostMapping("test")
    public void test() {
        tuShareSynchronizedDataTimer.syncDataFromNetworkToDesk();


    }
}
