package com.sample.banking.util;

import com.google.common.collect.ImmutableMap;
import com.sample.banking.BankingActivitiesImpls;
import com.sample.banking.BankingOrchestration;
import com.sample.banking.BankingOrchestrationImpl;
import com.sample.banking.model.Account;
import com.sample.banking.model.Amount;
import io.grpc.StatusRuntimeException;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowExecutionAlreadyStarted;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class Starter {
    public static void main(String[] args) {
        // set up client
        WorkflowServiceStubsOptions stubOptions =
                WorkflowServiceStubsOptions.newBuilder().setMetricsScope(Util.getMetricsScope()).build();
        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(stubOptions);
        WorkflowClient client = WorkflowClient.newInstance(service);

        // create worker
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker("DemoQueue");
        worker.registerWorkflowImplementationTypes(BankingOrchestrationImpl.class);
        worker.registerActivitiesImplementations(new BankingActivitiesImpls());
        factory.start();


        // start workflow exec
        String refid = "abcde"; //dummy
        Account from = new Account(998877);
        Account to = new Account(22334);
        Amount amount = new Amount(500);

        BankingOrchestration workflow = client.newWorkflowStub(BankingOrchestration.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("BankingTx-" + refid)
                        .setTaskQueue("DemoQueue")
                        .setSearchAttributes(ImmutableMap.of("TransferAmount", amount.getDollars()))
                        .build());


        try {
            WorkflowClient.start(workflow::transfer, from, to, refid, amount);
            WorkflowStub.fromTyped(workflow).getResult(Void.class);
        } catch (WorkflowExecutionAlreadyStarted e) {
            WorkflowStub.fromTyped(workflow).signal("completeTransaction");
            WorkflowStub.fromTyped(workflow).getResult(Void.class);
        }
    }
}
