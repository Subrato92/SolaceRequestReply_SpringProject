package com.subrato.packages.solace.RequestReplyDemo.config;

import com.solacesystems.jcsmp.*;
import com.subrato.packages.solace.RequestReplyDemo.pojo.InitializerPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class SolaceReplier {

    private MessageRouter router = null;

    private XMLMessageProducer producer = null;
    private XMLMessageConsumer cons = null;

    private CountDownLatch latch = null;
    private Topic topic = null;
    private Logger log = LoggerFactory.getLogger(SolaceReplier.class);

    public SolaceReplier(InitializerPayload payload, String topicRef){
        router = new MessageRouter(payload);
        String sessionStatus = router.connect();

        topic = JCSMPFactory.onlyInstance().createTopic(topicRef);
    }

    public void initialize(){
        initializeProducer();
        initializeConsumer();
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
            e.printStackTrace();
        }

    }

    private void initializeConsumer(){

        try {
            cons = router.getSession().getMessageConsumer(new XMLMessageListener() {
                @Override
                public void onReceive(BytesXMLMessage request) {
                    if (request.getReplyTo() != null) {
                        TextMessage reply = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
                        String text = "Sample response";
                        reply.setText(text);
                        try {
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
            e.printStackTrace();
        }

        try {
            router.getSession().addSubscription(topic);
            cons.start();

            System.out.println("Connected. Awaiting message...");
        } catch (JCSMPException e) {
            System.out.println("[Replier Connection Failed to Start]... " + e.getMessage());
        }


        // Consume-only session is now hooked up and running!
        try {
            latch.await(); // block here until message received, and latch will flip
        } catch (InterruptedException e) {
            System.out.println("I was awoken while waiting");
        }


    }


}
