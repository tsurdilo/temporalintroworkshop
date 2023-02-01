package com.sample.banking;

import com.sample.banking.model.Account;
import com.sample.banking.model.Amount;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class BankingOrchestrationImpl implements  BankingOrchestration {

    private BankingActivities accounts =
            Workflow.newActivityStub(BankingActivities.class,
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(2))
                            .setRetryOptions(RetryOptions.newBuilder()
                                    .setBackoffCoefficient(1)
                                    .build())
                            .build());
    private Amount amount;
    private boolean completeTx;

    @Override // Workflow Method
    public void transfer(Account from, Account to, String refid, Amount amount) {
        this.amount = amount;


        accounts.withdrawal(from, amount, refid);
        accounts.deposit(to, amount, refid);


        //Workflow.await(() -> completeTx);

    }

    @Override // SignalMethod
    public void completeTransaction() {
        this.completeTx = true;
    }

    @Override // QueryMethod
    public Amount getAmount() {
        return amount;
    }
}
