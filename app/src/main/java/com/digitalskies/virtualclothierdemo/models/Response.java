package com.digitalskies.virtualclothierdemo.models;

public class Response {

    private int responseCode;

    private int responseFor;

    private String responseMessage;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }


    public int getResponseFor() {
        return responseFor;
    }

    public void setResponseFor(int responseFor) {
        this.responseFor = responseFor;
    }
}
