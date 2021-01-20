package crs.fcl.integration.main;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestEmail {
	
	public static void main(String[] args) throws IOException {
		
	    System.out.println("SimpleEmail Start");
	    String emailID = "Richard.Wang@fcl.crs, wanglixing.ca@gmail.com";
	    
	    /**
	     * Send email with 
	     * @param toEmail
	     * @param subject
	     * @param body
	     * @param attachment
	     */
	    //EmailUtil.sendEmail(emailID, "SimpleEmail Testing Subject", "Hi, I am from Java program", null);

	    /**
	     * Send email with "Email object"
	     * @param Email 
	     */
	    Email email = new Email();
	    email.setFrom("no_reply@fcl.crs:NoReply-JD");
	    email.setTo(emailID);
	    email.setCc("wanglixing.ca@gmail.com");
	    email.setBcc("wanglixing.ca@hotmail.com");
	    email.setAllowReply(false);
	    email.setReplyToAddress("no_reply@fcl.crs");
	    email.setSubject("Best greeting to you");
	    email.setBodyMessage("Hello my dear client!");
	    
	    Attachment att0 = new Attachment("C:\\Users\\Richard.Wang\\Documents\\FCL Docs\\PCI DSS FAQ.docx");
	    email.getAttachments().add(att0);

	    Attachment att1 = new Attachment(new URL("file:///C:/Temp/srcDir/testFolder/test.txt"), "text/plain");
	    email.getAttachments().add(att1);
	    
	    String filePath = "C:\\Temp\\LookupNPullout.log";
        byte[] bFile = Files.readAllBytes(Paths.get(filePath));
	    Attachment att2 = new Attachment(bFile, "text/plain", "test.log");
	    email.getAttachments().add(att2);

	    Attachment att4 = new Attachment("C:\\Users\\Richard.Wang\\Documents\\FCL Docs\\How to use Global Protect.pdf", "application/pdf");
	    email.getAttachments().add(att4);

	    EmailUtil.sendEmail(email);
	}
}