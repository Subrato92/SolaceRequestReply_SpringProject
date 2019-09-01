package com.subrato.packages.solace.RequestReplyDemo.pojo;

public class RequestorPayload {

    private InitializerPayload propertiesPayload;
    private String topicName;

    public InitializerPayload getPropertiesPayload() {
        return propertiesPayload;
    }
    public void setPropertiesPayload(InitializerPayload propertiesPayload) {
        this.propertiesPayload = propertiesPayload;
    }
    public String getTopicName() {
        return topicName;
    }
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
