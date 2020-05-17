package com.cqq.stock.entity.vo;

import lombok.Data;

@Data
public class R<T> {
    private String msg = "success";
    private Integer code = 0;
    private T data;


    public static R successMsg(String msg) {
        R r = new R<>();
        r.setMsg(msg);
        return r;
    }

    public static <T> R successData(T t) {
        R r = new R<>();
        r.setData(t);
        return r;
    }
}
