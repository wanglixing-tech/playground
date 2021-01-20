package com.littlegui.activemq.ssl;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQSslConnectionFactory;

/**
 * Use SSL Connection QUeue Message COnsumer
 */
public class QueueMessageConsumer {

	public static void main(String[] args) throws JMSException, Exception {
		/*
		 * Configure parameter, key store, trust store, password, SSL connection SSL
		 */
        String keyStore = "C:\\apache-activemq-5.15.14\\conf\\client.ks";
        String trustStore = "C:\\apache-activemq-5.15.14\\conf\\client.ts";
        String keyStorePassword = "password";
        String url = "ssl://localhost:61714";

		ActiveMQSslConnectionFactory sslConnectionFactory = new ActiveMQSslConnectionFactory();
		sslConnectionFactory.setBrokerURL(url);
		sslConnectionFactory.setKeyAndTrustManagers(SSLUtils.loadKeyManager(keyStore, keyStorePassword),
				SSLUtils.loadTrustManager(trustStore), new java.security.SecureRandom());

		// Connect to ActiveMQ
		Connection conn = sslConnectionFactory.createConnection();
		conn.start();
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination dest = session.createQueue("sslDemo");
		// Configure consumer, print message with anonymous class
		MessageConsumer mc = session.createConsumer(dest);
		mc.setMessageListener(new MyQueueListener());

		// Not close the connection, keep it connected to ActiveMQ and receive messages
		// forever
	}
}
	class MyQueueListener implements MessageListener {
	@Override
	public void onMessage(Message msg) {
		if (msg instanceof TextMessage) {
			try {
				TextMessage tmsg = (TextMessage) msg;
				System.out.println(tmsg.getText());
			} catch (JMSException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println(msg.toString());
		}
	}
}