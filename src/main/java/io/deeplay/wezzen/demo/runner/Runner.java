package io.deeplay.wezzen.demo.runner;

import com.sun.net.httpserver.HttpServer;
import io.deeplay.wezzen.demo.consumer.RemoteServiceConsumer;
import io.deeplay.wezzen.demo.monitoring.Micrometer;
import io.deeplay.wezzen.demo.monitoring.Monitoring;
import io.deeplay.wezzen.demo.service.RemoteService;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public final class Runner {

    private static void startPrometheusServer(final PrometheusMeterRegistry registry) throws IOException {
        final HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/metrics", httpExchange -> {
            String response = registry.scrape();
            httpExchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
        server.start();
    }

    public static void main(final String[] args) throws InterruptedException, IOException {
        final RemoteService remoteService = new RemoteService();
        final PrometheusMeterRegistry meterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        startPrometheusServer(meterRegistry);
        final Monitoring monitoring = new Micrometer(meterRegistry);
        final Thread thread = new RemoteServiceConsumer(remoteService, monitoring);
        System.out.println("Starting work");
        thread.start();
        thread.join();
        System.out.println("End work");
    }

}
