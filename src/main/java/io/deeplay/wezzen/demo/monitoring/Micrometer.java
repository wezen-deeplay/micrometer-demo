package io.deeplay.wezzen.demo.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

public final class Micrometer implements Monitoring {

    private final MeterRegistry meterRegistry;

    public Micrometer(final MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void successfulQuery() {
        Counter.builder("demo.success.query.total")
                .tag("thread", Thread.currentThread().getName())
                .register(meterRegistry)
                .increment();
    }

    @Override
    public void failedQuery(final Throwable reason) {
        Counter.builder("demo.failed.query.total")
                .tag("thread", Thread.currentThread().getName())
                .tag("reason", reason.getMessage())
                .register(meterRegistry)
                .increment();
    }
}
