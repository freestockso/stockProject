package com.cqq.stock.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cqq.stock.entity.dto.DailyResult;
import com.cqq.stock.entity.dto.DailyParam;
import com.cqq.stock.entity.vo.R;
import com.cqq.stock.interfaces.TuShareParam;
import com.cqq.stock.util.InvokeUtil;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TuShareServiceTest {

    @Test
    public void hello() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        DailyParam dailyParam = new DailyParam();
        dailyParam.setTs_code("000001.SZ");
        dailyParam.setStart_date(DateUtil.format(DateUtil.offsetDay(DateUtil.date(), -1000), "yyyyMMdd"));
        dailyParam.setEnd_date(DateUtil.format(DateUtil.date(), "yyyyMMdd"));
        List<DailyResult> doing = doing(dailyParam, DailyResult.class);
        System.out.println(doing);


    }

    private <I extends TuShareParam, O> List<O> doing(I param, Class<O> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", "716d692ebb5b66be8b0ac3766ada39ae9ec0bf078cda25b720c32dcb");
        jsonObject.put("api_name", "daily");
        jsonObject.put("params", JSON.parseObject(JSON.toJSONString(param)));

        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(jsonObject, httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://api.waditu.com", requestEntity, String.class);
        String body = responseEntity.getBody();
        JSONObject root = JSON.parseObject(body);
        System.out.println(root);
        JSONObject data = root.getJSONObject("data");
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
                InvokeUtil.setSetMethodValue(dailyResult, field, jsonArray.getString(map.get(field.getName())));
            }
            list.add(dailyResult);
        }

        System.out.println(list);
        return list;
    }
}
