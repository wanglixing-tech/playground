package com.littlegui.activemq.ssl;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQSslConnectionFactory;

/**
 *    Use SSL connection
 */

public class QueueMessageProducer {
 	    public static void main(String[] args) throws JMSException, Exception {
	        /*
	         * Configure parameters, Key store, certifacate, key store password, SSL URL
	         */
	        String keyStore = "C:\\apache-activemq-5.15.14\\conf\\client.ks";
	        String trustStore = "C:\\apache-activemq-5.15.14\\conf\\client.ts";
	        String keyStorePassword = "password";
	        String url = "ssl://localhost:61714";

			// Create SSL Connection Factory
	        ActiveMQSslConnectionFactory sslConnectionFactory = new ActiveMQSslConnectionFactory();
	        sslConnectionFactory.setBrokerURL(url);
	        sslConnectionFactory.setKeyAndTrustManagers(SSLUtils.loadKeyManager(keyStore, keyStorePassword), SSLUtils.loadTrustManager(trustStore),
	                new java.security.SecureRandom());

	        // Connect to ActiveMQ
	        Connection conn = sslConnectionFactory.createConnection();
	        conn.start();
	        Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
	        Destination dest = session.createQueue("sslDemo");

	        // create message producer, send out a message
	        MessageProducer mp = session.createProducer(dest);
	        Message msg = session.createTextMessage("Hello SSL!");
	        mp.send(msg);
	        System.out.println("success");

	        // sending complete and close the connection
	        session.close();
	        conn.close();
	    }

	}