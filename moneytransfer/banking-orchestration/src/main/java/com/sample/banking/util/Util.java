package com.sample.banking.util;

import com.sample.banking.TransferException;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

import com.uber.m3.tally.RootScopeBuilder;
import com.uber.m3.tally.Scope;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.workflow.v1.WorkflowExecutionInfo;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.client.WorkflowClient;
import io.temporal.common.reporter.MicrometerClientStatsReporter;
import io.temporal.serviceclient.WorkflowServiceStubs;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Util {
    public static String sendGET(String port, String path) throws Exception {
        URL obj = new URL("http://localhost:" + port + path);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            throw new TransferException("transfer failed");
        }
    }

    public static HttpServer startPrometheusScrapeEndpoint(
            PrometheusMeterRegistry registry, int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext(
                    "/metrics",
                    httpExchange -> {
                        String response = registry.scrape();
                        httpExchange.sendResponseHeaders(200, response.getBytes(UTF_8).length);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(UTF_8));
                        }
                    });

            server.start();
            return server;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Scope getMetricsScope() {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        Scope scope =
                new RootScopeBuilder()
                        .reporter(new MicrometerClientStatsReporter(registry))
                        .reportEvery(com.uber.m3.util.Duration.ofSeconds(1));
        // Start the prometheus scrape endpoint
        HttpServer scrapeEndpoint = startPrometheusScrapeEndpoint(registry, 8077);
        // Stopping the worker will stop the http server that exposes the
        // scrape endpoint.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> scrapeEndpoint.stop(1)));

        return scope;
    }

    private static String getStatusAsString(WorkflowExecution execution, WorkflowClient client,
                                            WorkflowServiceStubs service) {
        DescribeWorkflowExecutionRequest describeWorkflowExecutionRequest =
                DescribeWorkflowExecutionRequest.newBuilder()
                        .setNamespace(client.getOptions().getNamespace())
                        .setExecution(execution)
                        .build();

        DescribeWorkflowExecutionResponse resp =
                service.blockingStub().describeWorkflowExecution(describeWorkflowExecutionRequest);

        WorkflowExecutionInfo workflowExecutionInfo = resp.getWorkflowExecutionInfo();
        return workflowExecutionInfo.getStatus().toString();
    }
}
