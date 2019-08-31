package com.subrato.packages.solace_app.config;

import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishEventHandler;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageProducer;
import com.subrato.packages.solace_app.pojos.PublishResponse;

public class Publisher {

	private XMLMessageProducer prod = null;
	private Topic topic;
	private TextMessage msg;
	
	public Publisher( JCSMPSession session , String topicName ) throws JCSMPException {
		
		prod = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {

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
		
		topic = JCSMPFactory.onlyInstance().createTopic(topicName);
		msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);		
	}
	
	public PublishResponse publish(String Message) {
		
		PublishResponse response = new PublishResponse();
		
		msg.setText(Message);
		try {
			prod.send(msg, topic);
			response.setStatus("Success");
		} catch (JCSMPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus("Failed - " + e.getMessage());
		}	
		
		response.setTopic(topic.getName());
		
		return response;
	}
	
	public void closePublisher() {
		if( prod != null ) {
			prod.close();
		}
	}
	
	protected void finalize() {
		closePublisher();
	}
	
}
