package com.sample.banking.model;

public class Account {

    private int id;

    public Account () {}

    public Account(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
