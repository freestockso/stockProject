package com.cqq.stock.entity.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
public class R<T> implements Serializable {
    public static final String SUCCESS_MSG = "success";
    private static final long serialVersionUID = 1L;
    public static final int FAIL_CODE = 1;
    public static final int SUCCESS_CODE = 0;

    @Getter
    @Setter
    private int code = 0;

    @Getter
    @Setter
    private String msg = SUCCESS_MSG;


    @Getter
    @Setter
    private T data;


    public R() {
        super();
    }

    public R(T data) {
        super();
        this.data = data;
    }


    public R(Throwable e) {
        super();
        this.msg = e.getMessage();
        this.code = FAIL_CODE;
    }

    public static <T> R<T> error(String msg) {
        R<T> r = new R<>();
        r.setMsg(msg);
        r.setCode(FAIL_CODE);
        return r;
    }

    public static <T> R<T> success(String msg, T data) {
        R<T> r = new R<>();
        r.setMsg(msg);
        r.setCode(SUCCESS_CODE);
        r.setData(data);
        return r;
    }

    public static <T> R<T> successMsg(String msg) {
        R<T> r = new R<>();
        r.setCode(SUCCESS_CODE);
        r.setMsg(msg);
        return r;
    }

    public R(T data, String msg, int code) {
        super();
        this.data = data;
        this.msg = msg;
        this.code = code;
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setData(data);
        return r;
    }


    public boolean hasError() {
        return this.code != SUCCESS_CODE;
    }

}
