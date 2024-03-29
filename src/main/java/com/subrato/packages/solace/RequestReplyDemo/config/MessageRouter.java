package com.subrato.packages.solace.RequestReplyDemo.config;


import com.solacesystems.jcsmp.*;
import com.subrato.packages.solace.RequestReplyDemo.pojo.InitializerPayload;


public class MessageRouter {

    private JCSMPProperties properties;
    private JCSMPSession session = null;

    public MessageRouter(InitializerPayload payload) {
        properties = new JCSMPProperties();

        properties.setProperty(JCSMPProperties.HOST, payload.getHost());
        properties.setProperty(JCSMPProperties.USERNAME, payload.getUserName());
        properties.setProperty(JCSMPProperties.VPN_NAME, payload.getVpn());
        properties.setProperty(JCSMPProperties.PASSWORD, payload.getPassword());

    }

    public String connect() {
        String response = null;

        if( session == null ) {
            response = initializeSession();
        }

        if( session!=null && !session.isClosed()) {
            try {
                session.connect();
                response = "Success - Connected";
            } catch (JCSMPException e) {
                session = null;
                response = "Failed To Connect " + e.getMessage();
            }
        }

        return response;
    }

    private String initializeSession() {
        String response;

        try {
            session = JCSMPFactory.onlyInstance().createSession(properties);
            response = "Success - Instance";
        } catch (InvalidPropertiesException e) {
            session = null;
            response = "Session Instance Creation Failed - " + e.getMessage();
        } catch (JCSMPException e) {
            session = null;
            response = "Session Instance Creation Failed - " + e.getMessage();
        }

        return response;
    }

    public JCSMPSession getSession() {
        return session;
    }

    public void killSession(){
        session.closeSession();
        session = null;
    }

}
