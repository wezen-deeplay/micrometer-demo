package io.deeplay.wezzen.demo.exceptions;

public class RemoteServiceAvailableException extends RemoteServiceException {

    private static final int ERROR_ID = 1002;

    public RemoteServiceAvailableException(final String message) {
        super(ERROR_ID, message);
    }

}
