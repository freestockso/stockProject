package com.cqq.stock.entity.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiqi.chen
 */
@Data
public class FilterVO {
    private int count;
    private List<String> codeList;
    public FilterVO(){
        this.codeList = new ArrayList<>();
    }
}
