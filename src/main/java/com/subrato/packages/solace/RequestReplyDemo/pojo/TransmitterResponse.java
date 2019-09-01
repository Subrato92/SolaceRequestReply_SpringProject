package com.subrato.packages.solace.RequestReplyDemo.pojo;

public class TransmitterResponse {

    private String log;
    private String reply;
    private boolean status;

    public TransmitterResponse(boolean status, String log, String reply){
        this.log = log;
        this.reply = reply;
        this.status = status;
    }

    public String getLog() {
        return log;
    }
    public String getReply() {
        return reply;
    }
    public boolean getStatus() {
        return status;
    }
}
