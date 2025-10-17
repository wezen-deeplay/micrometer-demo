package io.deeplay.wezzen.demo.monitoring;

import io.deeplay.wezzen.demo.exceptions.RemoteServiceException;

public interface Monitoring {

    void successfulQuery();

    void failedQuery(final RemoteServiceException reason);

    void unexpectedFailedQuery(final Throwable reason);
}
