package com.subrato.packages.solace.RequestReplyDemo.config;

import com.solacesystems.jcsmp.*;
import com.subrato.packages.solace.RequestReplyDemo.pojo.RequestorPayload;
import com.subrato.packages.solace.RequestReplyDemo.pojo.TransmitterResponse;

public class SolaceRequestor {

    private XMLMessageProducer producer = null;
    private XMLMessageConsumer consumer = null;
    private MessageRouter router = null;
    private boolean routerActive = false;
    private TextMessage textMessage = null;
    private Topic topic = null;

    public SolaceRequestor(RequestorPayload payload){
        if(payload.getPropertiesPayload() != null) {
            router = new MessageRouter(payload.getPropertiesPayload());
            router.connect();
            routerActive = true;
        }

        if(payload.getTopicName() != null) {
            topic = JCSMPFactory.onlyInstance().createTopic(payload.getTopicName());
            textMessage = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        }

        if(routerActive) {
            initializeProducer();
            initializeConsumer();
        }
    }

    private void initializeProducer(){
        if(!routerActive){
            return;
        }

        try {
            producer = router.getSession().getMessageProducer(new JCSMPStreamingPublishEventHandler() {

                @Override
                public void responseReceived(String messageID) {
                    System.out.println("Producer received response for msg: " + messageID);
                }

                @Override
                public void handleError(String messageID, JCSMPException e, long timestamp) {
                    System.out.printf("Producer received error for msg: %s@%s - %s%n",
                            messageID,timestamp,e);
                }
            });
        } catch (JCSMPException e) {
            e.printStackTrace();
        }

    }

    private void initializeConsumer(){
        if(!routerActive){
            return;
        }

        try {
            consumer = router.getSession().getMessageConsumer((XMLMessageListener)null);
            consumer.start();
        } catch (JCSMPException e) {
            e.printStackTrace();
        }
    }

    public TransmitterResponse transmitter(String message){

        String log = null;
        BytesXMLMessage reply = null;
        boolean status = false;
        textMessage.setText(message);
        int timeoutMs = 10000;

        if(!routerActive){
            return new TransmitterResponse(status, "[FAILED] Router Not Initialized", null);
        }

        try {
            Requestor requestor = router.getSession().createRequestor();
            reply = requestor.request(textMessage, timeoutMs, topic);
            status = true;
        } catch (JCSMPRequestTimeoutException e) {
            log = "Failed to receive a reply in " + timeoutMs + " msecs";
        } catch (JCSMPException e) {
            log = "[Failed] " + e.getMessage();
        }

        if(status) {
            return new TransmitterResponse(status, log, reply.toString());
        }

        return new TransmitterResponse(status, log, null);
    }

    public boolean isRouterActive() {
        return routerActive;
    }

    public void reset(){
        if(producer != null){
            producer.close();
            producer = null;
        }

        if(consumer != null){
            consumer.close();
            consumer = null;
        }

        if(router != null){
            router.killSession();
            router = null;
        }

        if(textMessage != null){
            textMessage.reset();
            textMessage = null;
        }

        routerActive = false;
        topic = null;
    }


}
