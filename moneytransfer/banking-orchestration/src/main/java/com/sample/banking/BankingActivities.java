package com.sample.banking;

import com.sample.banking.model.Account;
import com.sample.banking.model.Amount;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface BankingActivities {

    void withdrawal(Account from, Amount amount, String refid);

    void deposit(Account to, Amount amount, String refid);
}
