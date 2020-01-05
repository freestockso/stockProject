package com.cqq.stock.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqq.stock.constants.StockConstant;
import com.cqq.stock.entity.*;
import com.cqq.stock.mapper.StockInfoMapper;
import com.cqq.stock.mapper.StockTransactionInfoMapper;
import com.cqq.stock.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.min;

@Service
@AllArgsConstructor
@Slf4j
public class StockService extends ServiceImpl<StockInfoMapper, StockInfo> {

    private StockInfoMapper stockInfoMapper;
    private StockTransactionInfoMapper stockTransactionInfoMapper;

    public StockInfo selectByCode(String code) {
        log.info("select code is {}", code);
        List<StockInfo> stockInfos = stockInfoMapper.selectList(Wrappers.<StockInfo>query().lambda().eq(StockInfo::getCode, code));
        return stockInfos.get(0);
    }


    /**
     * 获取所有股票的代码,去除重复代码的股票
     *
     * @return codeList
     */
    public List<String> getAllCodeList() {

        if (StockConstant.stockList == null) {
            List<StockInfo> stockInfos = stockInfoMapper.selectList(null);
            StockConstant.stockList =
                    new ArrayList<>(new HashSet<>(stockInfos.stream().map(StockInfo::getCode).collect(Collectors.toList())));
        }

        return StockConstant.stockList;
    }


/*
    public List<StockTransactionInfo> getListByToday(String date) {
        return stockTransactionInfoMapper.listByDate("stock_transaction_info_2019", date);
    }
*/

    /**
     * 得到该date下的所有股票交易日数据
     *
     * @param date 20190630
     * @return list
     */
    public List<StockTransactionInfo> listByDate(long date) {
        LambdaQueryWrapper<StockTransactionInfo> condition = Wrappers.<StockTransactionInfo>query().lambda()
                .eq(StockTransactionInfo::getDate, date);
        return this.stockTransactionInfoMapper.selectList(condition);
    }

    /**
     * 设置股票是以厘或分为单位
     *
     * @param code sh000001
     */
    public void setTenTo1(String code) {
        if (stockInfoMapper.setTen(code) > 0) {
            log.info("success setTenTol");
        } else {
            log.info("error setTenTol, maybe has been set");
        }
    }

    /**
     * 计算股票的额外信息， 包括 K线图， 以及CCI
     *
     * @param code sh000001
     * @return list
     */
    public List<CalculateStockTransactionInfo> calculateStockAdditionalInformation(String code) {
        List<StockTransactionInfo> list = stockTransactionInfoMapper.getList(
                getTableNameThisYear(), code,
                TimeUtil.getLastYearNumber() * 10000, TimeUtil.getThisYearNumber() * 10000);
        List<StockTransactionInfo> list2 = stockTransactionInfoMapper.getList(
                getTableNameThisYear(), code,
                TimeUtil.getThisYearNumber() * 10000, TimeUtil.getDatetime());
        list.addAll(list2);
        List<CalculateStockTransactionInfo> main = CciUtil.main(list);
        KLineUtil.main(main, 120);
        calculateBuySalePointNumber(main);
        return main;

    }

    /**
     * 计算股票的买点个数与 卖点个数
     *
     * @param calculateStockTransactionInfos 某只股票的近期信息
     */
    private void calculateBuySalePointNumber(List<CalculateStockTransactionInfo> calculateStockTransactionInfos) {
        int buyPoint = 0;
        int salePoint = 0;
        for (int i = 0; i < calculateStockTransactionInfos.size(); i++) {
            double cci = calculateStockTransactionInfos.get(i).getCci();
            buyPoint += cci < -100 ? 1 : 0;
            salePoint += cci > 100 ? 1 : 0;

        }
        log.info("买点个数:{} 买点率{},卖点数{}, 卖点率{}",
                buyPoint,
                buyPoint * 1.0 / calculateStockTransactionInfos.size(),
                salePoint,
                salePoint * 1.0 / calculateStockTransactionInfos.size()
        );
    }

    /**
     * 查询已经到达买入点，并且已经开始上涨的股票
     *
     * @return list
     */
    public List<StockInfo> getCanBuyAndGoUpStock() {
        return stockInfoMapper.getCanBuyStock(-100).stream().filter(stockInfo -> {
            List<StockTransactionInfo> stockTransactionInfos = stockTransactionInfoMapper.getList("stock_transaction_info_2019", stockInfo.getCode(), 2019_0000, 2019_1017);
            if (stockTransactionInfos.size() < 2) {
                return false;
            }
            StockTransactionInfo lastStock = stockTransactionInfos.get(stockTransactionInfos.size() - 1);
            StockTransactionInfo beforeLastStock = stockTransactionInfos.get(stockTransactionInfos.size() - 2);
            double upRate = (lastStock.getClose() - beforeLastStock.getClose()) * 100.0 / beforeLastStock.getClose();
            return upRate > 2.00;
        }).collect(Collectors.toList());
    }

    public String syncDataFrom2DatabaseNetwork() {


        return "success";
    }

    /**
     * 从现在的网络中获取股票的实时数据
     *
     * @return list
     */
    public List<StockTransactionInfo> getAllStockFromNowNetwork() {
        return StockInfoAdapter.getStockTransactionInfoByCodeList(this.getAllCodeList());
    }

    /**
     * 删除 数据库 中 的 数据
     *
     * @param date 20181231
     * @return 成功删除
     */
    public boolean deleteByDate(long date) {
        return stockTransactionInfoMapper.delete(Wrappers.<StockTransactionInfo>query().lambda().eq(StockTransactionInfo::getDate, date)) > 0;
    }

    public boolean deleteBetweenDate(long startDate,long endDate) {
        return stockTransactionInfoMapper.delete(
                Wrappers.<StockTransactionInfo>query().lambda()
                        .ge(StockTransactionInfo::getDate, startDate)
                        .le(StockTransactionInfo::getDate, endDate)
        ) > 0;
    }
    public List<StockRecent> getToDayAllStockRecentByCodeList() {
        return StockInfoAdapter.getStockRecentByCodeList(this.getAllCodeList());
    }

    public List<StockInfo> getStockList() {
        return new ArrayList<>(new HashSet<>(this.stockInfoMapper.selectList(null)));
    }

    /**
     * 股市的基本信息Map
     *
     * @return {code:name}
     */
    public Map<String, StockInfo> getStockMap() {
        List<StockInfo> stockList = this.getStockList();
        Map<String, StockInfo> map = new HashMap<>();
        stockList.forEach(s -> {
            map.put(s.getCode(), s);
        });
        return map;
    }

    public List<StockTransactionInfo> getCciListByCode(String code) {
        List<StockTransactionInfo> list = this.stockTransactionInfoMapper.selectList(Wrappers.<StockTransactionInfo>lambdaQuery().eq(StockTransactionInfo::getCode, code));
        return list.stream().skip(28).collect(Collectors.toList());
//        StockInfo stockInfo = this.stockTransactionInfoMapper.selectByCode(code);

    }


    /**
     * 计算股票的DIF
     *
     * @param code
     * @return
     */
    public List<EMAStock> calculateDIF(String code) {
        String tableName = getTableNameThisYear();
        List<StockTransactionInfo> list = this.stockTransactionInfoMapper.getList(tableName, code, 2019_0000, 2019_1300);
        List<EMAStock> collect = list.stream().map(EMAStock::new).collect(Collectors.toList());
        EMACalculateUtil.calculateAll(collect, 12, 26, 9);
        return collect;

    }

    private String getTableNameThisYear() {
        return String.format("stock_transaction_info_%d", TimeUtil.getThisYearNumber());
    }

    private String getTableNameThisYear(int n) {
        return String.format("stock_transaction_info_%d", n);
    }


    public List<StockInfo> getMACDGoodStock() {
        List<String> allCodeList = this.getAllCodeList();
        List<StockInfo> codes = new ArrayList<>();
        int size = allCodeList.size();
        for (int i = 0; i < min(size, size); i++) {
            String code = allCodeList.get(i);
            StockInfo stockInfo = this.selectByCode(code);
            List<StockTransactionInfo> list = this.stockTransactionInfoMapper.getList(getTableNameThisYear(), code, 2019_0000, 2019_1300);
            List<EMAStock> collect = list.stream().map(EMAStock::new).collect(Collectors.toList());
            EMACalculateUtil.calculateAll(collect, 12, 26, 9);
            if (!collect.isEmpty() && collect.get(collect.size() - 1).getMacd() > 0.5) {
                codes.add(stockInfo);
            }
            log.info(String.format("正在使用MACD指标进行分析股票,现在已经分析了%d/%d只股票(%.2f%%)", i + 1, size, (i + 1) * 1.0 / size * 100));

        }
        return codes;
    }


    public List<StockTransactionInfo> calculateCCI(String code) {
        List<StockTransactionInfo> list =
                this.stockTransactionInfoMapper.getList(getTableNameThisYear(), code, 2019_0000, 2019_1300);
        CciUtil.mainAndOrigin(list);
        list.forEach(System.out::println);
        return list;

    }

    /**
     * 将CCI的值计入数据库中
     */
    public void recordCCI() {
        String code = "sh603606";

        List<String> allCodeList = getAllCodeList();
        List<StockTransactionInfo> lastYear = this.stockTransactionInfoMapper.getList(
                getTableNameThisYear(TimeUtil.getLastYearNumber()), code, TimeUtil.getLastYearBeginDate(), TimeUtil.getLastYearEndDate()
        );
        List<StockTransactionInfo> thisYear = this.stockTransactionInfoMapper.getList(
                getTableNameThisYear(), code, TimeUtil.getThisYearBeginDate(), TimeUtil.getThisYearEndDate()
        );
        lastYear.addAll(thisYear);
        CciUtil.mainAndOrigin(lastYear);

        long currentTimeMillis = System.currentTimeMillis();
        System.out.println("开始时间");

        this.stockTransactionInfoMapper.updateStockListCCIByCodeAndDate(
                getTableNameThisYear(), lastYear);

        System.out.println("finish:" + (System.currentTimeMillis() - currentTimeMillis));


    }


    /**
     * 使用CCI低买高卖法计算一只股票通过这种方法，能够赚多少钱
     */
    public void getMoney(String code) {
        List<StockTransactionInfo> list = this.stockTransactionInfoMapper.getList(
                getTableNameThisYear(), code, TimeUtil.getThisYearBeginDate(), TimeUtil.getThisYearEndDate()
        );
        int earnTimes = 0;
        int allTimes = 0;
        double allEarn = 0;

        for (int i = 0; i < list.size(); i++) {
            StockTransactionInfo buyTimeStock = list.get(i);
            if (buyTimeStock.getCci() <= -100) {
                for (int j = i + 1; j < list.size(); j++) {
                    StockTransactionInfo saleTimeStock = list.get(j);
                    if (saleTimeStock.getCci() >= 100) {
                        System.out.println(String.format("买入时间：%d,卖出时间%d", buyTimeStock.getDate(), saleTimeStock.getDate()));
                        double earn = 100.0 * (saleTimeStock.avg() - buyTimeStock.avg()) / buyTimeStock.avg();
                        earnTimes += earn > 0 ? 1 : 0;
                        allEarn += earn;
                        allTimes++;
                        i = j + 1;
                        break;
                    }
                }
            }
        }
        System.out.println(String.format("总共赚%f%%,总操作次数%d次,赚钱次数%d,成功率%f%%",
                allEarn, allTimes, earnTimes, earnTimes * 100.0 / allTimes));


    }

    public double guess(String code, long x) {
        //  设 open = close = high = close = X  CCI = f(open,close,high,low)  不知道open,close,high,low
        //令open = close = high = close = X,则 CCI = f(x1,x2,x3,X) = -100 ,求解出x
        // 那么根据方程，推出当X = k时， CCI <= -100,则我第2天价格只要不超过k，则买入
        // 那么根据方程，推出当X = k2时， CCI >= 100,则我第2天价格只要不低于k2，则卖出
        List<StockTransactionInfo> list = this.stockTransactionInfoMapper.getList(
                getTableNameThisYear(), code, TimeUtil.getThisYearBeginDate(), TimeUtil.getThisYearEndDate()
        );
        return calculate(x, list);

    }

    private double calculate(long x, List<StockTransactionInfo> list) {
        ArrayList<StockTransactionInfo> copyList = new ArrayList<>(list);
        StockTransactionInfo e = new StockTransactionInfo();
        e.setOpen(x);
        e.setClose(x);
        e.setHigh(x);
        e.setLow(x);
        copyList.add(e);
        CciUtil.mainAndOrigin(copyList);
        return copyList.get(copyList.size() - 1).getCci();
    }

    public GoodPricePoint autoGuess(String code) {
        List<StockTransactionInfo> list = this.stockTransactionInfoMapper.getList(
                getTableNameThisYear(), code, TimeUtil.getThisYearBeginDate(), TimeUtil.getThisYearEndDate()
        );
        return getGoodPricePoint(code, list);
    }

    private GoodPricePoint getGoodPricePoint(String code, List<StockTransactionInfo> list) {
        GoodPricePoint goodPricePoint = new GoodPricePoint();
        goodPricePoint.setCode(code);
        goodPricePoint.setBuyPrice(guessMinPrice(list));
        goodPricePoint.setSalePrice(guessMaxPrice(list));
        return goodPricePoint;
    }

    static int count = 0;

    /**
     * 计算所有股票的下个交易日的合适买入点和卖出点,并且更新到库里
     */
    public void autoGuessAll() {
        List<StockTransactionInfo> list = this.stockTransactionInfoMapper.selectAll(getTableNameThisYear());
        Map<String, List<StockTransactionInfo>> map = list.stream().collect(Collectors.groupingBy(StockTransactionInfo::getCode));

        int mapSize = map.size();
        map.forEach((code, ls) -> {
            count++;
            GoodPricePoint goodPricePoint = getGoodPricePoint(code, ls);
            List<StockInfo> stockInfos = this.stockInfoMapper.selectList(Wrappers.<StockInfo>query().lambda().eq(StockInfo::getCode, code));
            for (int i = 0; i < stockInfos.size(); i++) {
                stockInfos.get(i).setBuyPrice((long) goodPricePoint.getBuyPrice());
                stockInfos.get(i).setSalePrice((long) goodPricePoint.getSalePrice());
                this.stockInfoMapper.updateById(stockInfos.get(i));
            }
            System.out.println(String.format("finish %d/%d(%f%%)", count, mapSize, count * 100.0 / mapSize));
        });

    }


    private double guessMinPrice(List<StockTransactionInfo> list) {
        if (list.isEmpty()) {
            return 999999999;
        }
        StockTransactionInfo lastStock = list.get(list.size() - 1);
        long left = 0L;
        long right = lastStock.getHigh() * 2;
        double calculate;
        do {
            long mid = (left + right) >> 1;
            calculate = calculate(mid, list);
            if (right - left <= 1) {
                break;
            } else if (calculate < -103) {
                left = mid;
            } else if (calculate > -101) {
                right = mid;
            }
        } while (calculate < -103 || calculate > -101);
        return left;
    }

    private double guessMaxPrice(List<StockTransactionInfo> list) {
        if (list.isEmpty()) {
            return 999999999;
        }
        StockTransactionInfo lastStock = list.get(list.size() - 1);
        long left = 0L;
        long right = lastStock.getHigh() * 2;
        double calculate;
        do {
            long mid = (left + right) >> 1;
            calculate = calculate(mid, list);
            if (right - left <= 1) {
                break;
            } else if (calculate < 101) {
                left = mid;
            } else if (calculate > 103) {
                right = mid;
            }
        } while (calculate < 101 || calculate > 103);
        return left;
    }

    static int times = 0;
    static int success = 0;

    /**
     * 计算如果回踩5日线
     * 计算得证，如果回踩5日线进行买入,胜率为49%,不值得操作
     *
     * @param code
     */
    public void calculateHui5DayLine(String code, Integer day) {
        List<StockTransactionInfo> list = this.stockTransactionInfoMapper.selectAll(getTableNameThisYear());
//        List<StockTransactionInfo> collect = list.stream().filter(s -> s.getCode().equals(code)).collect(Collectors.toList());
        Map<String, List<StockTransactionInfo>> map = list.stream().collect(Collectors.groupingBy(StockTransactionInfo::getCode));
        map.forEach((c, collect) -> {
            Map<Integer, Long> mean5Line = new HashMap<>();
            int DAY = day == null ? 5 : day;
            for (int i = DAY - 1; i < collect.size(); i++) {
                long sum = 0;
                for (int j = 0; j < DAY; j++) {
                    sum += collect.get(i - j).getClose();
                }
                mean5Line.put(i, sum / DAY);
            }
            for (int i = DAY + DAY; i < collect.size(); i++) {
                boolean otherDayIsLow = true;
                for (int j = 1; j < DAY; j++) {
                    boolean beHigh = collect.get(i - j).getClose() >= mean5Line.get(i - j);
                    if (beHigh) {
                        otherDayIsLow = false;
                    }
                }
                boolean toDayHigh = collect.get(i).getClose() >= mean5Line.get(i);
                if (toDayHigh && otherDayIsLow) {
                    System.out.println(collect.get(i) + ":" + mean5Line.get(i) + "回踩5日线");
                    times++;
                    if (i + 1 < collect.size() && collect.get(i + 1).getClose() > collect.get(i).getClose()) {
                        System.out.println("回踩成功");
                        success++;
                    }
                } else {
                    System.out.println(collect.get(i) + ":" + mean5Line.get(i));
                }
            }
        });
        System.out.println(String.format("all time is %d,and success times is %d, rate is %.2f%%", times, success, success * 100.0 / times));
        success = 0;
        times = 0;
    }


    public List<StockTransactionInfo> getListAfterDate(long date) {
        return this.stockTransactionInfoMapper.selectList(Wrappers.<StockTransactionInfo>query().lambda().gt(StockTransactionInfo::getDate, date));
    }

    public List<StockTransactionInfo> getListBetween(long startDate, long endDate) {
        return this.stockTransactionInfoMapper.selectList(
                Wrappers.<StockTransactionInfo>query().lambda()
                        .ge(StockTransactionInfo::getDate, startDate)
                        .le(StockTransactionInfo::getDate, endDate)
        );
    }
}
