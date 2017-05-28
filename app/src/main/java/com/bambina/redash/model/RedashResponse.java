package com.bambina.redash.model;

/**
 * Created by hirono-mayuko on 2017/05/11.
 */

public class RedashResponse {
    private Redash mRedash;

    private boolean isSuccessful;

    private boolean isSocketTimeoutException;

    private boolean isUnknownHostException;

    private String mErrorMessage;

    public RedashResponse(){
        mRedash = new Redash();
    }

    public Redash getRedash() {
        return mRedash;
    }

    public void setRedash(Redash redash) {
        this.mRedash = redash;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public boolean isSocketTimeoutException() {
        return isSocketTimeoutException;
    }

    public void setSocketTimeoutException(boolean socketTimeoutException) {
        isSocketTimeoutException = socketTimeoutException;
    }

    public boolean isUnknownHostException() {
        return isUnknownHostException;
    }

    public void setUnknownHostException(boolean unknownHostException) {
        isUnknownHostException = unknownHostException;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.mErrorMessage = errorMessage;
    }
}
