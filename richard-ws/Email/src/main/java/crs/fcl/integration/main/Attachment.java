package crs.fcl.integration.main;

public class Attachment {
	 /* 
	  * Default MIME type, it must be specified only in case you send attachments in ByteArray format
	  */
	private String mimeType = "text/plain";  
	/*
	 * An attached Object can be any of Sting, URL or a byte[]
	 */
	private Object attachedObject = null;
	/*
	 * attachmentName, it must be specified only in case you send attachments in ByteArray format
	 */
	private String attachmentName = null;
	
	/**
	 * Constructor with a path to attachment, default MIME type is text/plain
	 * @param attachedObject A String path to the file you are going to attach to the email
	 */
	public Attachment(Object attachedObject) {
		this.attachedObject = attachedObject;
	}
	
	/**
	 * Constructor with path to attachment file and its MIME type
	 * @param attachedObject
	 * @param mimeType
	 */
	public Attachment(Object attachedObject, String mimeType) {
		this.mimeType = mimeType;
		this.attachedObject = attachedObject;
	}
	
	/**
	 * Constructor with a path to attachment, default MIME type is text/plain and the attachment name
	 * This is used in case you want to send your email with byte[] formatted attachment.
	 * 
	 * @param attachedObject A String path to the file you
	 * @param attachedObject
	 * @param mimeType
	 * @param attachmentName
	 */
	public Attachment(Object attachedObject, String mimeType, String attachmentName) {
		this.mimeType = mimeType;
		this.attachedObject = attachedObject;
		this.attachmentName = attachmentName;
	}

	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public Object getAttachedObject() {
		return attachedObject;
	}
	public void setAttachedObject(Object attachedObject) {
		this.attachedObject = attachedObject;
	}
	public String getAttachmentName() {
		return attachmentName;
	}
	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

}
