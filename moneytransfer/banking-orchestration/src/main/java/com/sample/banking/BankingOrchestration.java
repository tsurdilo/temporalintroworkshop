package com.sample.banking;

import com.sample.banking.model.Account;
import com.sample.banking.model.Amount;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface BankingOrchestration {
    @WorkflowMethod
    void transfer(Account from,
                  Account to,
                  String refid,
                  Amount amount);

    @SignalMethod
    void completeTransaction();

    @QueryMethod
    Amount getAmount();
}
