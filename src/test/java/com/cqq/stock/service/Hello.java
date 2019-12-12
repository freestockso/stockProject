package com.cqq.stock.service;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Hello {
    public static void  main(String...args){
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(5);
        list.add(null);
        List<Studnet> collect = list.stream().map(s -> {
            if (s % 2 == 0) {
                Studnet studnet = new Studnet();
                studnet.setId(s);
                return studnet;
            } else {
                return null;
            }
        }).collect(Collectors.toList());
        System.out.println(collect);
    }
}
@Data
class Studnet{
    int id;
}
