package fr.nicknqck.utils;

import lombok.Getter;
@Getter
public class TripleMap<A, B, C> {


    private A first;
    private B second;
    private C third;
    public TripleMap(A a, B b, C c) {
        this.first = a;
        this.second = b;
        this.third = c;
    }
    public TripleMap() {
    }

    public void put(A a, B b, C c) {
        this.first = a;
        this.second = b;
        this.third = c;
    }
    public void remove(Object object) {
        if (this.first.equals(object)) {
            this.first = null;
        } else if (this.second.equals(object)) {
            this.second = null;
        } else if (this.third.equals(object)) {
            this.third = null;
        }
    }
}