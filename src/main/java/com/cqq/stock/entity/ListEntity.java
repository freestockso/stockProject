package com.cqq.stock.entity;

import lombok.Data;

import java.util.List;

@Data
public class ListEntity<T> {

    private List<T> list;
    private int size;

    public ListEntity(List<T> list) {
        this.list = list;
        this.size = list.size();

    }
}
