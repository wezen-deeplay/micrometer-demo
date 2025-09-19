package io.deeplay.wezzen.demo.consumer;

import io.deeplay.wezzen.demo.monitoring.Monitoring;
import io.deeplay.wezzen.demo.service.RemoteService;

import java.util.Optional;

public final class RemoteServiceConsumer extends Thread {

    private final RemoteService remoteService;

    private final Monitoring monitoring;

    public RemoteServiceConsumer(final RemoteService remoteService, final Monitoring monitoring) {
        this.remoteService = remoteService;
        this.monitoring = monitoring;
    }

    private Optional<Long> getRandomLong(final long min, final long max) {
        try {
            final long remoteRandomLong = remoteService.getRemoteRandomLong(min, max);
            monitoring.successfulQuery();
            return Optional.of(remoteRandomLong);
        } catch (final Throwable throwable) {
            monitoring.failedQuery(throwable);
            return Optional.empty();
        }
    }

    @Override
    public void run() {
        long start, end;
        while (true) {
            start = System.currentTimeMillis();
            final Optional<Long> randomLong = getRandomLong(1000L, 1000000000L);
            end = System.currentTimeMillis();
            if (randomLong.isEmpty()) {
                System.out.println("query failed for some reason");
            } else {
                System.out.printf("random value: %d, spent time: %d%n", randomLong.get(), end - start);
            }
        }
    }
}
