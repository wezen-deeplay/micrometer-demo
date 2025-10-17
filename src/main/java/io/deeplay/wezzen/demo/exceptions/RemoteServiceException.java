package io.deeplay.wezzen.demo.exceptions;

public abstract class RemoteServiceException extends Exception {

    private final int errorId;

    public RemoteServiceException(final int errorId, final String message) {
        super(message);
        this.errorId = errorId;
    }

    public int getErrorId() {
        return errorId;
    }
}
