package io.deeplay.wezzen.demo.exceptions;

public class RemoteServiceExecutionException extends RemoteServiceException {

    private static final int ERROR_ID = 1001;

    public RemoteServiceExecutionException(final String message) {
        super(ERROR_ID, message);
    }
}
