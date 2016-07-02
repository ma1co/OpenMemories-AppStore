package com.github.ma1co.openmemories.appstore;

public class Try<T> {
    private final T result;
    private final Exception exception;

    public Try(T result) {
        this.result = result;
        this.exception = null;
    }

    public Try(Exception exception) {
        this.result = null;
        this.exception = exception;
    }

    public boolean isSuccessful() {
        return exception == null;
    }

    public T getResult() {
        return result;
    }

    public Exception getException() {
        return exception;
    }
}
