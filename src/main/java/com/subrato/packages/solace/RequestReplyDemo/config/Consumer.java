package com.subrato.packages.solace_app.config;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageConsumer;
import com.solacesystems.jcsmp.XMLMessageListener;

public class Consumer {

	private XMLMessageConsumer cons = null;
	private CountDownLatch latch = null;
	private Topic topic = null;
	private ArrayList<String> receivedMsg;

	public Consumer(JCSMPSession session, String topicRef) throws JCSMPException {

        receivedMsg = new ArrayList<String>();
		topic = JCSMPFactory.onlyInstance().createTopic(topicRef);
        latch = new CountDownLatch(1); // used for
                                       // synchronizing b/w threads
        /** Anonymous inner-class for MessageListener
         *  This demonstrates the async threaded message callback */
        cons = session.getMessageConsumer(new XMLMessageListener() {
            @Override
            public void onReceive(BytesXMLMessage msg) {
                if (msg instanceof TextMessage) {
                    System.out.printf("TextMessage received: '%s'%n",
                            ((TextMessage)msg).getText());
                    receivedMsg.add(((TextMessage)msg).getText());
                } else {
                    System.out.println("Message received.");
                }
                System.out.printf("Message Dump:%n%s%n",msg.dump());
                latch.countDown();  // unblock main thread
            }

            @Override
            public void onException(JCSMPException e) {
                System.out.printf("Consumer received exception: %s%n",e);
                latch.countDown();  // unblock main thread
            }
        });
        
        session.addSubscription(topic);
        System.out.println("Connected. Awaiting message...");
        cons.start();
        // Consume-only session is now hooked up and running!

        try {
            latch.await(); // block here until message received, and latch will flip
        } catch (InterruptedException e) {
            System.out.println("I was awoken while waiting");
        }

	}
	
	public String getMessage() throws JCSMPException {
		
		StringBuilder sb = new StringBuilder();

        sb.append("[MESSAGE] : ");

		while(!receivedMsg.isEmpty()){
		    sb.append(receivedMsg.remove(0));
		    sb.append(" - ");
        }

		return sb.toString();
	}

	public void closeConnection(){
        // Close consumer
        cons.close();
    }

}
