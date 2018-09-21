package com.redhelmet.alert2me.data.remote;

public enum AppNetworkError {
    RESPONSE_FAILURE(new Throwable("Response is failure.")),
    PASTE_ERROR(new Throwable("Error when paste Json response.")),
    RESPONSE_NULL(new Throwable("Response is null.")),
    REQUEST_FAILURE(new Throwable("Request is failure.")),
    ;

    Throwable throwable;

    AppNetworkError(Throwable throwable) {
        this.throwable = throwable;
    }
}
