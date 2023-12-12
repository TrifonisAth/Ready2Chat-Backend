package com.socialmedia.socialmedia.utility;

public class PairImpl <T, U> implements Pair<T, U> {
    private T first;
    private U second;

    public PairImpl(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public PairImpl(T first) {
        this.first = first;
        this.second = null;
    }

    public PairImpl() {}

    @Override
    public T getFirst() {
        return null;
    }

    @Override
    public U getSecond() {
        return null;
    }

    @Override
    public void setFirst(T first) {
        this.first = first;
    }

    @Override
    public void setSecond(U second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Pair)) return false;
        PairImpl<?, ?> p = (PairImpl<?, ?>) o;
        return p.first.equals(first) && p.second.equals(second);
    }
}
