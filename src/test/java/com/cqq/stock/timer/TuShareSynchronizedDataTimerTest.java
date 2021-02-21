package com.cqq.stock.timer;

import com.alibaba.fastjson.JSON;
import com.cqq.stock.entity.Fund;
import com.cqq.stock.util.MySpider;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Slf4j
public class TuShareSynchronizedDataTimerTest {

    public static final String DATA_NET_WORTH_TREND = "Data_netWorthTrend = ";


    @Test
    public void test() {
        go();
    }

    private List<Fund> getStockList() {
        MySpider m = new MySpider();
        m.setUseCache(true);
        m.setFileURL("D:\\fund.txt");
        String doing = m.doing("http://fund.eastmoney.com/pingzhongdata/570008.js?v=20201029224108");
        int data_netWorthTrend = doing.indexOf(DATA_NET_WORTH_TREND);
        doing = doing.substring(data_netWorthTrend + DATA_NET_WORTH_TREND.length());
        int i = doing.indexOf("]");
        String substring = doing.substring(0, i + 1);
        List<Fund> funds = JSON.parseArray(substring, Fund.class);
        funds.forEach(s -> {
            Date date = new Date();
            date.setTime(s.getX());
            s.setDate(new SimpleDateFormat("yyyy-MM-dd").format(date));
        });
        return funds;
    }

    public void go() {
        List<Fund> stockList = getStockList();
        //初始金额
        double money = 500;
        double firstRate = 0.5;
        double up = 0.05;
        double down = -0.05;
        double nowHave = 0;
        boolean first = true;
        double actualRate = 0;
        double addRate = 0.1;
//        double addRateInfo = 0.1;
        double lastPrice = 0;
        for (int i = 0; i < stockList.size() - 1; i++) {
            Fund todayFund = stockList.get(i);
            Fund tomornyFund = stockList.get(i + 1);
            if (first) {
                double spend = money * firstRate;
                double add = spend / tomornyFund.getY();
                nowHave += add;
                money -= spend;
                lastPrice = tomornyFund.getY();
                first = false;
                show(stockList, nowHave, i, tomornyFund, add, money);
            }
            if (big(stockList.get(i).getY(), lastPrice, up)) {
                if (actualRate < 0) {
                    actualRate = 0;
                }
                actualRate += addRate;
                double sum = money + nowHave * todayFund.getY();
                double needSpend = sum * actualRate;


            }
        }

    }

    private boolean big(Double fundPrice, Double lastPrice, double rate) {
        return (lastPrice - fundPrice) / fundPrice >= rate;
    }

    private void show(List<Fund> stockList, double nowHave, int i, Fund fund, double add, double money) {
        log.info("{} day, your buy {}, this price is {}, your have {}, last buy price is {}, your money becoming {}", fund.getDate(), add, stockList.get(i).getY(), nowHave, stockList.get(i + 1).getY(), money);
    }

}
