package com.sample.banking.model;

public class Amount {
    int dollars;

    public Amount() {}

    public Amount(int dollars) {
        this.dollars = dollars;
    }

    public int getDollars() {
        return dollars;
    }

    public void setDollars(int dollars) {
        this.dollars = dollars;
    }
}
