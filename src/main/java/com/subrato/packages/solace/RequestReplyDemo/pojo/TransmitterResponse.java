package com.subrato.packages.solace.RequestReplyDemo.pojo;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.DeliveryMode;
import com.solacesystems.jcsmp.Destination;
import com.solacesystems.jcsmp.TextMessage;

public class TransmitterResponse {

    private String log;
    private String replyText;
    private boolean status;
    private ReplyBody replyBody;

    public TransmitterResponse(boolean status, String log, BytesXMLMessage reply){
        this.log = log;
        this.status = status;

        if(reply != null) {

            if (reply instanceof TextMessage) {
                this.replyText = ((TextMessage)reply).getText();
            } else {
                this.replyText = "Message received.";
            }

            replyBody = new ReplyBody(reply);
        }else{
            this.replyText = "No Message received.";
        }

    }

    public String getLog() {
        return log;
    }
    public String getReplyText() {
        return replyText;
    }
    public boolean isStatus() {
        return status;
    }
    public ReplyBody getReplyBody() {
        return replyBody;
    }
}
