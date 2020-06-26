package com.cqq.stock.entity.vo;

import lombok.Data;

@Data
public class R<T> {
    private String msg = "success";
    private Integer code = 0;
    private T data;


    public static <T> R<T> successMsg(String msg) {
        R<T> r = new R<>();
        r.setMsg(msg);
        return r;
    }

    public static <T> R<T> successData(T t) {
        R<T> r = new R<>();
        r.setData(t);
        return r;
    }

    public static<T> R<T> error(String msg) {
        R<T> tr = new R<>();
        tr.setCode(1);
        tr.setMsg(msg);
        return tr;
    }
}
