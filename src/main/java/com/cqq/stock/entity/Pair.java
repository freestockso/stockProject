package com.cqq.stock.entity;


import java.util.Objects;

public class Pair<F, S> {
    private F first;
    private S second;
    public Pair() {
    }
    public Pair(F first,S second){
        this.first = first;
        this.second = second;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return first.equals(pair.first) &&
                second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
