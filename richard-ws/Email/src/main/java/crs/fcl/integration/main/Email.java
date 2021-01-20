package crs.fcl.integration.main;

import java.util.ArrayList;

/**
 * This is a POJO to describe one specific email to be sent out. 
 * Of which, some of them are set from Adapter's properties or SYstem properties; 
 * also some of them may be provided as JVM arguments from command-line
 * 
 * @author Richard.Wang
 *
 */
public class Email {
	private String from = "";
	private String to = "";
	private String cc = "";
	private String bcc = "";
	private boolean allowReply = false;
	private String replyToAddress = "";
	private String subject = "";
	private String bodyMessage = "";
	private ArrayList<Attachment> attachments = new ArrayList<Attachment>();

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	public String getTo() {
		return to;
	}
	
	public void setTo(String to) {
		this.to = to;
	}
	
	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public boolean isAllowReply() {
		return allowReply;
	}

	public void setAllowReply(boolean allowReply) {
		this.allowReply = allowReply;
	}

	public String getReplyToAddress() {
		return replyToAddress;
	}

	public void setReplyToAddress(String replyToAddress) {
		this.replyToAddress = replyToAddress;
	}

	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBodyMessage() {
		return bodyMessage;
	}
	
	public void setBodyMessage(String bodyMessage) {
		this.bodyMessage = bodyMessage;
	}

	public ArrayList<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(ArrayList<Attachment> attachments) {
		this.attachments = attachments;
	}
}
