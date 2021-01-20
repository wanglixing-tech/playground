package crs.fcl.integration.main;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 * This is a utility program for sending email from automation project.
 * 
 * @author Richard.Wang
 * @version 0.1  2019/09/12
 * 
 */
public class EmailUtil {
	private static Properties props;
	private static Session session;
	private final static String smtpHostServer = "smtp.res.ad.crs";

	static {
		props = System.getProperties();
		props.put("mail.smtp.host", smtpHostServer);
	}

	/**
	 * Utility method to send simple HTML email
	 * 
	 * @param toEmail
	 * @param subject
	 * @param body
	 * @param attachment
	 */
	public static void sendEmail(String toEmail, String subject, String body, String path2Attachment) {
		try {

			session = Session.getInstance(props, null);

			MimeMessage msg = new MimeMessage(session);
			// set message headers
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");

			msg.setFrom(new InternetAddress("no_reply@fcl.crs", "NoReply-JD"));
			msg.setReplyTo(InternetAddress.parse("no_reply@fcl.crs", false));
			msg.setSubject(subject);
			
			// Create a multi-part message
			Multipart multipart = new MimeMultipart();

			// Part-1: Create the message part
			BodyPart messagePart = new MimeBodyPart();
			messagePart.setText(body);
			multipart.addBodyPart(messagePart);

			if(path2Attachment != null && !path2Attachment.isEmpty()) {
				// Part-2: Attachment
				BodyPart attachmentPart = new MimeBodyPart();
				DataSource source = new FileDataSource(path2Attachment);
				attachmentPart.setDataHandler(new DataHandler(source));
				attachmentPart.setFileName(source.getName());
				multipart.addBodyPart(attachmentPart);
			}
			// Send the complete message parts
			msg.setContent(multipart);

			msg.setSentDate(new Date());
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
			System.out.println("Sending......");
			Transport.send(msg);
			System.out.println("E-Mail has been sent Successfully!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * Utility method to send simple HTML email
	 * 
	 * @param mail an Email object in which you can set detailed items for composing of the email.
	 */
	public static void sendEmail(Email mail) {		
		try {
			session = Session.getInstance(props, null);
			MimeMessage msg = new MimeMessage(session);
			// set message headers
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");
			String[] fromAddressAndName = mail.getFrom().split(":");
			msg.setFrom(new InternetAddress(fromAddressAndName[0], fromAddressAndName[1]));			
			msg.setReplyTo(InternetAddress.parse(mail.getReplyToAddress(), mail.isAllowReply()));
			msg.setSubject(mail.getSubject());
			
			// Create a multi-part message
			Multipart multipart = new MimeMultipart();

			// Part-1: Create the message part
			BodyPart messagePart = new MimeBodyPart();
			messagePart.setText(mail.getBodyMessage());
			multipart.addBodyPart(messagePart);
			ArrayList<Attachment> atts= mail.getAttachments();
			if(atts.size() > 0) {
				for (Attachment oneAtt : atts) {
					BodyPart attachmentPart = new MimeBodyPart();
					Object attachedContent = oneAtt.getAttachedObject();
					if ( attachedContent instanceof String) {
						/*
						 * The attachment is provided with path to attachment file  
						 */
						DataSource source = new FileDataSource((String)attachedContent);						
						attachmentPart.setDataHandler(new DataHandler(source));
						attachmentPart.setFileName((new File(((String)attachedContent)).getName()));
						multipart.addBodyPart(attachmentPart);
					} else {
						if ( attachedContent instanceof URL) {
							/*
							 * The attachment is provided with URL 
							 */
							DataSource source = new URLDataSource((URL)attachedContent);						
							attachmentPart.setDataHandler(new DataHandler(source));
							attachmentPart.setFileName((new File(((URL)attachedContent).getFile()).getName()));
							multipart.addBodyPart(attachmentPart);
						} else {
							if (attachedContent instanceof byte[]) {
								/*
								 * The attachment is provided with byte[] 
								 */
					            DataSource source = new ByteArrayDataSource((byte[])attachedContent, oneAtt.getMimeType());
								attachmentPart.setDataHandler(new DataHandler(source));
								attachmentPart.setFileName(oneAtt.getAttachmentName());
								multipart.addBodyPart(attachmentPart);
							} 
						}
					}
				}
			}
			
			// Send the complete message parts
			msg.setContent(multipart);

			msg.setSentDate(new Date());
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.getTo(), false));
			msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(mail.getCc(), false));
			msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(mail.getBcc(), false));
			System.out.println("Sending......");
			Transport.send(msg);
			System.out.println("E-Mail has been sent Successfully!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}