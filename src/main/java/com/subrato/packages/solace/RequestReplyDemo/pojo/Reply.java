package com.subrato.packages.solace.RequestReplyDemo.pojo;

public class Reply {
    private String log;
    private String reply;

    public Reply(String log, String reply){
        this.log = log;
        this.reply = reply;
    }

    public String getLog() {
        return log;
    }

    public String getReply() {
        return reply;
    }
}
