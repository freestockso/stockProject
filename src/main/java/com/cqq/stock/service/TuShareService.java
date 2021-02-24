package com.cqq.stock.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cqq.stock.entity.dto.*;
import com.cqq.stock.entity.vo.FilterVO;
import com.cqq.stock.entity.vo.R;
import com.cqq.stock.entity.vo.TradeCalVO;
import com.cqq.stock.interfaces.TuShareParam;
import com.cqq.stock.util.BatchUtil;
import com.cqq.stock.util.InvokeUtil;
import com.cqq.stock.util.UicodeBackslashU;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * http://tushare.org/trading.html
 * <p>
 * https://waditu.com/document/2
 *
 * @author qiqi.chen
 */
@Slf4j
@Service
@AllArgsConstructor
public class TuShareService {

    public static final String STOCK = "stock:";
    public static final String STOCK_BAK = "stock_bak:";
    public static final String STOCK_BASIC = "stock_basic";
    public static final int BATCH_SIZE = 7;
    private StringRedisTemplate redisTemplate;

    /**
     * 获取日线数据
     *
     * @param dailyParam dailyParam
     * @return R
     */
    private R<List<DailyResult>> daily(DailyParam dailyParam) {
        List<DailyResult> doing;
        try {
            doing = requestList(dailyParam, DailyResult.class, "daily");
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            return R.error(e.getMessage());
        }
        return R.ok(doing);
    }

    public R<Map<String, List<DailyResult>>> dailyMore(DailyParam dailyParam) {
        Map<String, List<DailyResult>> map = new HashMap<>();
        String[] codeList = dailyParam.getTs_code().split(",");
        List<String> need = new ArrayList<>();

        for (String code : codeList) {
            String res = redisTemplate.opsForValue().get(STOCK + code);
            if (res == null) {
                need.add(code);
            } else {
                map.put(code, JSON.parseArray(res, DailyResult.class));
            }
        }
        int sum = (need.size() / BATCH_SIZE) + (need.size() % BATCH_SIZE == 0 ? 0 : 1);
        BatchUtil.with(need, BATCH_SIZE).toDo((i, ls) -> {
            long start = System.currentTimeMillis();
            log.info("第{}/{}批数据开始加载", i, sum);
            dailyParam.setTs_code(String.join(",", ls));
            R<List<DailyResult>> daily = daily(dailyParam);
            List<DailyResult> data = daily.getData();
            Map<String, List<DailyResult>> newMap = data.stream().collect(Collectors.groupingBy(DailyResult::getTs_code));
            newMap.forEach((k, v) -> {
                v.sort(Comparator.comparing(DailyResult::getTrade_date));
                map.put(k, v);
                redisTemplate.opsForValue().set(STOCK + k, JSON.toJSONString(v), 1, TimeUnit.DAYS);
            });
            long end = System.currentTimeMillis();
            log.info("第{}批数据加载完毕,耗时:{}ms,完成进度:{}%", i, end - start, i * 100 / sum);
        });


        return R.ok(map);
    }

    /**
     * 获取交易日历
     *
     * @return return
     */
    public R<List<TradeCalVO>> tradeCal() {
        try {
            TradeCalParam param = new TradeCalParam();
            param.setStart_date("20200101");
            param.setEnd_date("20300101");
            param.setExchange("SSE");
            String time = redisTemplate.opsForValue().get("time");
            if (time == null) {
                List<TradeCalVO> tradeCal = requestList(param, TradeCalVO.class, "trade_cal");
                redisTemplate.opsForValue().set("time", JSON.toJSONString(tradeCal));
                return R.ok(tradeCal);
            } else {
                return R.ok(JSON.parseArray(time, TradeCalVO.class));
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            return R.error(e.toString());
        }
    }

    /**
     * 获取前面若干个开市的时间
     *
     * @return
     */
    public R<List<String>> getDay(String baseDay, int offset) {
        R<List<TradeCalVO>> listR = tradeCal();
        if (listR.hasError()) {
            return R.error(listR.getMsg());
        }
        List<TradeCalVO> data = listR.getData();
        int k = -1;
        for (int i = 0; i < data.size(); i++) {

            TradeCalVO tradeCalVO = data.get(i);
            if (baseDay.equals(tradeCalVO.getCal_date())) {
                k = i;
                break;
            }
        }
        if (k == -1) {
            return R.error("k == -1 ? ");
        }
        List<String> list = new ArrayList<>();
        for (int i = k; i >= 0; i--) {
            TradeCalVO tradeCalVO = data.get(i);
            if ("1".equals(tradeCalVO.getIs_open())) {
                list.add(tradeCalVO.getCal_date());
            }
            if (list.size() >= offset) {
                break;
            }
        }
        return R.ok(list);
    }


    public R<List<StockBasicResult>> stockBasic() {
        StockBasicParam param = new StockBasicParam();
        param.setList_status("L");
        try {
            String str = redisTemplate.opsForValue().get(STOCK_BASIC);
            if (str == null) {
                List<StockBasicResult> stockBasic = requestList(param, StockBasicResult.class, "stock_basic");
                redisTemplate.opsForValue().set(STOCK_BASIC, JSON.toJSONString(stockBasic), 30, TimeUnit.DAYS);
                return R.ok(stockBasic);
            } else {
                List<StockBasicResult> stockBasic = JSON.parseArray(str, StockBasicResult.class);
                return R.ok(stockBasic);
            }

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            return R.error(e.toString());
        }
    }

    public R<List<StockComplexInfo>> stockBak(StockBakParam stockBakParam) {
        try {
            List<StockComplexInfo> bakDaily = requestList(stockBakParam, StockComplexInfo.class, "bak_daily");
            return R.ok(bakDaily);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            return R.error(e.toString());
        }

    }

//    public R<Map<String, List<StockComplexInfo>>> stockBakMore(StockBakParam stockBakParam) {
//        Map<String, List<StockComplexInfo>> map = new HashMap<>();
//        String[] codeList = stockBakParam.getTs_code().split(",");
//        List<String> need = new ArrayList<>();
//
//        for (String code : codeList) {
//            String res = redisTemplate.opsForValue().get(STOCK_BAK + code);
//            if (res == null) {
//                need.add(code);
//            } else {
//                map.put(code, JSON.parseArray(res, StockComplexInfo.class));
//            }
//        }
//        int sum = (need.size() / BATCH_SIZE) + (need.size() % BATCH_SIZE == 0 ? 0 : 1);
//        BatchUtil.with(need, BATCH_SIZE).toDo((i, ls) -> {
//            long start = System.currentTimeMillis();
//            log.info("第{}/{}批数据开始加载", i, sum);
//            stockBakParam.setTs_code(String.join(",", ls));
//            R<List<StockComplexInfo>> daily = stockBak(stockBakParam);
//            List<StockComplexInfo> data = daily.getData();
//            Map<String, List<StockComplexInfo>> newMap = data.stream().collect(Collectors.groupingBy(StockComplexInfo::getTs_code));
//            newMap.forEach((k, v) -> {
//                v.sort(Comparator.comparing(StockComplexInfo::getTrade_date));
//                map.put(k, v);
//                redisTemplate.opsForValue().set(STOCK_BAK + k, JSON.toJSONString(v), 1, TimeUnit.DAYS);
//            });
//            long end = System.currentTimeMillis();
//            log.info("第{}批数据加载完毕,耗时:{}ms,完成进度:{}%", i, end - start, i * 100 / sum);
//        });
//
//
//        return R.ok(map);
//    }

    public R<String> loadDaily() {
        R<List<StockBasicResult>> listR = stockBasic();
        List<StockBasicResult> data = listR.getData();
        String codeList = data.stream().map(StockBasicResult::getTs_code).collect(Collectors.joining(","));
        DailyParam dailyParam = new DailyParam();
        dailyParam.setTs_code(codeList);
        dailyParam.setStart_date(DateUtil.format(DateUtil.offsetDay(DateUtil.date(), -1000), "yyyyMMdd"));
        dailyParam.setEnd_date(DateUtil.format(DateUtil.date(), "yyyyMMdd"));
        dailyMore(dailyParam);
        return R.ok(null);
    }

    public R<String> loadStockBak() {
        R<List<StockBasicResult>> listR = stockBasic();
        List<StockBasicResult> data = listR.getData();
        String codeList = data.stream().map(StockBasicResult::getTs_code).collect(Collectors.joining(","));
        StockBakParam stockBakParam = new StockBakParam();
        stockBakParam.setTs_code(codeList);
        stockBakParam.setStart_date(DateUtil.format(DateUtil.offsetDay(DateUtil.date(), -1000), "yyyyMMdd"));
        stockBakParam.setEnd_date(DateUtil.format(DateUtil.date(), "yyyyMMdd"));
//        stockBakMore(stockBakParam);
        return R.ok(null);
    }

    private <I extends TuShareParam, O> List<O> requestList(I param, Class<O> clazz, String apiName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", "716d692ebb5b66be8b0ac3766ada39ae9ec0bf078cda25b720c32dcb");
        jsonObject.put("api_name", apiName);
        jsonObject.put("params", JSON.parseObject(JSON.toJSONString(param)));

        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(jsonObject, httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://api.waditu.com", requestEntity, String.class);
        String body = responseEntity.getBody();
        JSONObject root = JSON.parseObject(body);
        JSONObject data = root.getJSONObject("data");
        if (data == null) {
            System.out.println(body);
            System.out.println(UicodeBackslashU.unicodeToCn(body));
        }
        JSONArray items = data.getJSONArray("items");
        JSONArray fields = data.getJSONArray("fields");
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            map.put(fields.get(i).toString(), i);
        }
        R<List<DailyResult>> r = new R<>();
        r.setCode(root.getInteger("code"));
        r.setMsg(root.getString("msg"));
        Field[] declaredFields = clazz.getDeclaredFields();
        List<O> list = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            O dailyResult = clazz.newInstance();
            JSONArray jsonArray = items.getJSONArray(i);
            for (Field field : declaredFields) {
                Integer index = map.get(field.getName());
                if (index != null) {
                    InvokeUtil.setSetMethodValue(dailyResult, field, jsonArray.getString(index));
                }
            }
            list.add(dailyResult);
        }

        return list;
    }

    /**
     * 过滤程序
     *
     * @param filterDTO
     * @return
     */
    public R<FilterVO> filter(FilterDTO filterDTO) {
        R<List<StockBasicResult>> listR = this.stockBasic();
        List<StockBasicResult> stockBasicResults = listR.getData();
        FilterVO filterVO = new FilterVO();
        BatchUtil.with(stockBasicResults, 500).toDo((i, ls) -> {
            long start = System.currentTimeMillis();
            DailyParam dailyParam = new DailyParam();
            dailyParam.setTs_code(ls.stream().map(StockBasicResult::getTs_code).collect(Collectors.joining(",")));
            dailyParam.setStart_date(DateUtil.format(DateUtil.offsetDay(DateUtil.date(), -1000), "yyyyMMdd"));
            dailyParam.setEnd_date(DateUtil.format(DateUtil.date(), "yyyyMMdd"));
            R<Map<String, List<DailyResult>>> mapR = dailyMore(dailyParam);
            Map<String, List<DailyResult>> data = mapR.getData();
            for (StockBasicResult l : ls) {

                List<DailyResult> list = data.get(l.getTs_code());
                if (list.size() < 30) {
                    continue;
                }
                DailyResult d1 = list.get(list.size() - 1);
                DailyResult d2 = list.get(list.size() - 2);


                if (filterDTO.getMinPrice() != null && Double.parseDouble(d1.getClose()) < filterDTO.getMinPrice()) {
                    continue;
                }

                if (filterDTO.getMaxPrice() != null && Double.parseDouble(d1.getClose()) > filterDTO.getMaxPrice()) {
                    continue;
                }

                //涨幅 单位 %
                double v = (Double.parseDouble(d1.getClose()) - Double.parseDouble(d2.getClose())) * 100 / Double.parseDouble(d2.getClose());

                if (filterDTO.getMinChange() != null && v < filterDTO.getMinChange()) {
                    continue;
                }
                if (filterDTO.getMaxPrice() != null && v > filterDTO.getMaxChange()) {
                    continue;
                }
                filterVO.setCount(filterVO.getCount() + 1);
                System.out.println(l.getTs_code());
            }


            long end = System.currentTimeMillis();
            log.info("这批股票的过滤时间花费为:{}s", (end - start) / 1000);
        });
        return R.ok(filterVO);
    }
}
