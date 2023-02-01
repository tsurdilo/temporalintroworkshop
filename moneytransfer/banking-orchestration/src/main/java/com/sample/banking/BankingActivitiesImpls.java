package com.sample.banking;

import com.sample.banking.model.Account;
import com.sample.banking.model.Amount;
import com.sample.banking.util.Util;
import io.temporal.activity.Activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BankingActivitiesImpls implements BankingActivities {

    @Override
    public void withdrawal(Account from, Amount amount, String refid) {
        try {
            Util.sendGET("8083", "/withdrawal/" + amount.getDollars() + "/" + from.getId());
        } catch (Exception e) {
            throw Activity.wrap(e);
        }
    }

    @Override
    public void deposit(Account to, Amount amount, String refid) {
        try {
            Util.sendGET("8084", "/deposit/" + amount.getDollars() + "/" + to.getId());
        } catch (Exception e) {
            throw Activity.wrap(e);
        }
    }
}
