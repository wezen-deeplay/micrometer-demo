package io.deeplay.wezzen.demo.service;

import io.deeplay.wezzen.demo.exceptions.RemoteServiceAvailableException;
import io.deeplay.wezzen.demo.exceptions.RemoteServiceExecutionException;

import java.util.Random;

public final class RemoteService {

    private static final int MIN_LATENCY_MILLISECONDS = 1000;
    private static final int MAX_LATENCY_MILLISECONDS = 3000;

    private final Random random = new Random();

    private void tryThrowRemoteServiceAvailableException() throws RemoteServiceAvailableException {
        if (random.nextDouble() > 0.9) {
            throw new RemoteServiceAvailableException("Invalid operation");
        }
    }

    private void tryThrowRemoteServiceExecutionException() throws RemoteServiceExecutionException {
        if (random.nextDouble() > 0.8) {
            throw new RemoteServiceExecutionException("Invalid parameter");
        }
    }

    public long getRemoteRandomLong(final long min, final long max) throws RemoteServiceAvailableException, RemoteServiceExecutionException {
        final long value = random.nextLong(min, max);
        final int latency = random.nextInt(MIN_LATENCY_MILLISECONDS, MAX_LATENCY_MILLISECONDS);
        try {
            Thread.sleep(latency);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
        tryThrowRemoteServiceAvailableException();
        tryThrowRemoteServiceExecutionException();
        return value;
    }

}
