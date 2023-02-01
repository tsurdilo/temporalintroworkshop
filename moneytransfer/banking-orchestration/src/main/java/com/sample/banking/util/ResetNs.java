package com.sample.banking.util;

import com.google.protobuf.Duration;
import io.grpc.StatusRuntimeException;
import io.temporal.api.enums.v1.IndexedValueType;
import io.temporal.api.operatorservice.v1.AddSearchAttributesRequest;
import io.temporal.api.operatorservice.v1.DeleteNamespaceRequest;
import io.temporal.api.workflowservice.v1.RegisterNamespaceRequest;
import io.temporal.serviceclient.OperatorServiceStubs;
import io.temporal.serviceclient.OperatorServiceStubsOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.util.Collections;

public class ResetNs {
    private static final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
    private static final OperatorServiceStubs operatorService = OperatorServiceStubs.newServiceStubs(
            OperatorServiceStubsOptions.newBuilder()
                    .setChannel(service.getRawChannel())
                    .validateAndBuildWithDefaults());

    public static void main(String[] args) {
        // for cleanup we just delete default ns and recreate it..its simpler that way
        operatorService.blockingStub().deleteNamespace(
                DeleteNamespaceRequest.newBuilder()
                        .setNamespace("default")
                        .build()
        );

        service.blockingStub().registerNamespace(RegisterNamespaceRequest.newBuilder()
                .setNamespace("default")
                .setWorkflowExecutionRetentionPeriod(Duration.newBuilder()
                        .setSeconds(2 * 86400) // 2 days
                        .build())
                .build());

        try {
            operatorService.blockingStub().addSearchAttributes(AddSearchAttributesRequest.newBuilder()
                    .putAllSearchAttributes(Collections.singletonMap("TransferAmount", IndexedValueType.INDEXED_VALUE_TYPE_INT))
                    .build());
        } catch (StatusRuntimeException e) {
            // ignore..already registered SA
        }

    }
}
