package com.subrato.packages.solace.RequestReplyDemo.pojo;

public class StringResponse {
    private String message;
    private boolean status;

    public StringResponse(String message, boolean status){
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }
}
