package com.amazonaws.samples;
import java.util.List;
import java.util.Map.Entry;

//import javax.jms.ConnectionFactory;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SimpleQueueServiceSample {

    public static void main(String[] args) throws Exception {

        String accessKey = "AKIAXF4NKM5GN7FB35FC"; 
        String secretKey = "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";
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        SQSConnectionFactory connectionFactory =
            new SQSConnectionFactory(new ProviderConfiguration(),
                                     AmazonSQSClientBuilder.standard()
                                                           .withRegion(Regions.US_EAST_2)
                                                           .withCredentials(new AWSStaticCredentialsProvider(awsCreds)));
        SQSConnection connection = (SQSConnection) connectionFactory.createConnection();
     // Get the wrapped client
        AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();
        // Create an SQS queue named MyQueue, if it doesn't already exist
        String myQueueUrl = null;
        if (!client.queueExists("MyQueue2")) {
        	System.out.println("Queue 'MyQueue2' does not exist, try to create it......");
        	myQueueUrl = client.createQueue("MyQueue2").getQueueUrl();
        	System.out.println("Created with URL:" + myQueueUrl);
        } else {
        	System.out.println("Queue 'MyQueue2' has already been there.");        	
        }
  
        // Send a message
        System.out.println("Sending a message to MyQueue2.\n");
        client.sendMessage(new SendMessageRequest(myQueueUrl, "This is my message text."));

        // Receive messages
        System.out.println("Receiving messages from MyQueue2.\n");
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
        List<Message> messages = client.receiveMessage(receiveMessageRequest).getMessages();
        for (Message message : messages) {
            System.out.println("  Message");
            System.out.println("    MessageId:     " + message.getMessageId());
            System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
            System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
            System.out.println("    Body:          " + message.getBody());
            for (Entry<String, String> entry : message.getAttributes().entrySet()) {
                System.out.println("  Attribute");
                System.out.println("    Name:  " + entry.getKey());
                System.out.println("    Value: " + entry.getValue());
            }
        }
        System.out.println();

        
    }
}
