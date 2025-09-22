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

    private static final int PORT = 8081;

    private static void startPrometheusServer(final PrometheusMeterRegistry registry) throws IOException {
        final HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/metrics", httpExchange -> {
            String response = registry.scrape();
            httpExchange.getResponseHeaders().set("Content-Type", "text/plain; version=0.0.4; charset=utf-8");
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
        final int threadsNum = 5;
        final Thread[] threads = new Thread[threadsNum];
        for (int i = 0; i < threadsNum; i++) {
            final Thread thread = new RemoteServiceConsumer(remoteService, monitoring);
            threads[i] = thread;
            thread.start();
        }
        System.out.println("Starting work");
        for (final Thread thread : threads) {
            thread.join();
        }
        System.out.println("End work");
    }

}
