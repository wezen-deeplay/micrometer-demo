package io.deeplay.wezzen.demo.monitoring;

public interface Monitoring {

    void successfulQuery();

    void failedQuery(final Throwable reason);
}
