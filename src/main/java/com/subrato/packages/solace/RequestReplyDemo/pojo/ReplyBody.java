package com.subrato.packages.solace.RequestReplyDemo.pojo;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.DeliveryMode;
import com.solacesystems.jcsmp.Destination;
import com.solacesystems.jcsmp.TextMessage;

public class ReplyBody {
    private String correlationId;
    private int priority;
    private String destination;
    private String deliveryMode;
    private String messageId;
    private String replyTo;
    private String reply;

    public ReplyBody(BytesXMLMessage reply){
        correlationId = reply.getCorrelationId();
        priority = reply.getPriority();

        if(reply.getDestination() != null) {
            destination = reply.getDestination().getName();
        }else{
            destination = "null";
        }

        if(reply.getDeliveryMode() != null) {
            deliveryMode = reply.getDeliveryMode().name();
        }else{
            deliveryMode = "null";
        }

        if(reply.getReplyTo() != null) {
            replyTo = reply.getReplyTo().getName();
        }else{
            replyTo = "null";
        }

        messageId = reply.getMessageId();
        this.reply = new String(reply.getBytes());
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append("ReplyMessage: "+reply);
        sb.append(System.getProperty("line.separator"));
        sb.append("Destination: " + destination );
        sb.append(System.getProperty("line.separator"));
        sb.append("ReplyTo: " + replyTo );
        sb.append(System.getProperty("line.separator"));
        sb.append("DeliveryMode: " + deliveryMode );
        sb.append(System.getProperty("line.separator"));
        sb.append("MessageId: " + messageId );
        sb.append(System.getProperty("line.separator"));
        sb.append("CorrelationId: " + correlationId );

        return sb.toString();
    }

    public String getCorrelationId() {
        return correlationId;
    }
    public int getPriority() {
        return priority;
    }
    public String getDestination() {
        return destination;
    }
    public String getDeliveryMode() {
        return deliveryMode;
    }
    public String getMessageId() {
        return messageId;
    }
    public String getReplyTo() {
        return replyTo;
    }
    public String getReply() {
        return reply;
    }
}
