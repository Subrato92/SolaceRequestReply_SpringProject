package com.subrato.packages.solace.RequestReplyDemo.config;

import com.solacesystems.jcsmp.*;
import com.subrato.packages.solace.RequestReplyDemo.pojo.InitializerPayload;
import com.subrato.packages.solace.RequestReplyDemo.pojo.RequestorReplierPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;

public class SolaceReplier {

    private MessageRouter router = null;
    private XMLMessageProducer producer = null;
    private XMLMessageConsumer cons = null;
    private boolean isActive = false;
    private CountDownLatch latch = null;
    private Topic topic = null;
    private Logger log = LoggerFactory.getLogger(SolaceReplier.class);

    public SolaceReplier(RequestorReplierPayload payload){
        if(payload.getPropertiesPayload() != null) {
            router = new MessageRouter(payload.getPropertiesPayload());
            String sessionStatus = router.connect();
            isActive = true;
            latch = new CountDownLatch(1);
        }

        if( payload.getTopicName() != null ) {
            topic = JCSMPFactory.onlyInstance().createTopic(payload.getTopicName());
        }

        if( isActive ) {
            initializeProducer();
            initializeConsumer();
        }
    }

    private void initializeProducer(){

        try {
            producer = router.getSession().getMessageProducer(new JCSMPStreamingPublishEventHandler() {
                @Override
                public void responseReceived(String messageID) {
                    System.out.println("Producer received response for msg: " + messageID);
                }

                @Override
                public void handleError(String messageID, JCSMPException e, long timestamp) {
                    System.out.printf("Producer received error for msg: %s@%s - %s%n", messageID, timestamp, e);
                }
            });
        } catch (JCSMPException e) {
            log.info("[Exception] @ProducerInitialization : " +e.getMessage());
            isActive = false;
            router = null;
        } catch (NullPointerException e){
            log.info("[Exception] @ProducerInitialization : " +e.getMessage());
            isActive = false;
            router = null;
        }

    }

    //Initialized to produce Auto-Reply
    private void initializeConsumer(){

        try {
            cons = router.getSession().getMessageConsumer(new XMLMessageListener() {

                @Override
                public void onReceive(BytesXMLMessage request) {
                    if (request.getReplyTo() != null) {
                        TextMessage reply = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
                        String text;
                        if (request instanceof TextMessage){
                            text = "[HEADER] Message Received on " + LocalDateTime.now().toString() + "\n" +
                                    "[MESSAGE]" + ((TextMessage)request).getText() ;
                        }else{
                            text = "[HEADER] Message Received on " + LocalDateTime.now().toString() + "\n" +
                                    "[MESSAGE]" + request.dump() ;
                        }
                        reply.setText(text);
                        try {
                            //--- Exchange of Message
                            producer.sendReply(request, reply);
                        } catch (JCSMPException e) {
                            System.out.println("Error sending reply.");
                        }
                    } else {
                        System.out.println("Received message without reply-to field");
                    }
                    latch.countDown();  // unblock main thread
                }

                @Override
                public void onException(JCSMPException e) {
                    System.out.printf("Consumer received exception: %s%n",e);
                    latch.countDown();  // unblock main thread
                }
            });
        } catch (JCSMPException e) {
            log.info("[Exception] @ConsumerInitialization : " +e.getMessage());
            isActive = false;
            router = null;
        } catch (NullPointerException e){
            log.info("[Exception] @ConsumerInitialization : " +e.getMessage());
            isActive = false;
            router = null;
        }

        try {
            router.getSession().addSubscription(topic);
            cons.start();

            log.info("Connected. Awaiting message...");
        } catch (JCSMPException e) {
            log.info("[Exception] Replier Connection Failed to Start... " + e.getMessage());
        } catch (NullPointerException e){
            log.info("[Exception] @TopicSubscription : " +e.getMessage());
            isActive = false;
            router = null;
        }


        // Consume-only session is now hooked up and running!
        try {
            latch.await(); // block here until message received, and latch will flip
        } catch (InterruptedException e) {
            log.info("I was awoken while waiting");
        }

    }

    public void reset(){
        if( router != null ) {
            router.killSession();
            router = null;
        }

        if( producer!=null ) {
            producer.close();
            producer = null;
        }

        if( cons != null ) {
            cons.close();
            cons = null;
        }

        isActive = false;
        latch = null;
        topic = null;
    }


}
