package crs.fcl.esb.mq;

/***************************************************************************/
/*                                                                         */
/* // Sample Command Line Parameters									   */
/*  -h 127.0.0.1 -p 1414 -c TEST.CHL -m MQA1 -q TEST.Q1                    */
/*                                                                         */
/***************************************************************************/

import com.ibm.mq.*; // Include the MQ package
import com.ibm.mq.constants.CMQC;
import java.io.*;
import java.util.Calendar;
import java.util.Hashtable;
//import java.util.logging.Logger;

public class MQbrowse2 {
	// private Logger logger = Logger.getLogger(MQbrowse.class.getName());
	private Hashtable<String, String> params = null;
	// private Hashtable<String, Object> mqht = null;
	private static String myQmgr = null;
	private static String myQueue = null;
	private static MQQueueManager _qMgr = null;

	public static void main(String args[]) throws IOException {
		MQbrowse2 mqBrowse = new MQbrowse2();
		try {
			mqBrowse.init(args);
			_qMgr = new MQQueueManager(myQmgr);
			mqBrowse.start();
		} catch (IllegalArgumentException e) {
			System.out.println("Usage: java MQBrowse -m QueueManagerName -q QueueName");
			System.exit(1);
		} catch (MQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.exit(0);
	}

	/**
	 * Make sure the required parameters are present.
	 * 
	 * @return true/false
	 */
	private boolean allParamsPresent() {
		boolean b = params.containsKey("-m") && params.containsKey("-q");
		return b;
	}

	/**
	 * Extract the command-line parameters and initialize the MQ variables.
	 * 
	 * @param args
	 * @throws IllegalArgumentException
	 */
	private void init(String[] args) throws IllegalArgumentException {
		params = new Hashtable<String, String>();
		if (args.length > 0 && (args.length % 2) == 0) {
			for (int i = 0; i < args.length; i += 2) {
				params.put(args[i], args[i + 1]);
			}
		} else {
			throw new IllegalArgumentException();
		}

		if (allParamsPresent()) {
			myQmgr = (String) params.get("-m");
			myQueue = (String) params.get("-q");

		} else {
			throw new IllegalArgumentException();
		}
	}

	public void start() {
		// Query the queue current Depth first, if it > 0, then goto read, otherwise,
		// show no message
		try {
			if (getQueueCurrentDepth() != 0) {
				/******************************************************/
				/* Set up our options to browse for the first message */
				/******************************************************/
				int openOptions = CMQC.MQOO_BROWSE + CMQC.MQOO_FAIL_IF_QUIESCING;
				MQQueue browseQueue = null;

				browseQueue = _qMgr.accessQueue(myQueue, openOptions, null, null, null);
				System.out.println("\n OPEN - '" + myQueue + "'\n\n");

				MQGetMessageOptions gmo = new MQGetMessageOptions();
				gmo.options = CMQC.MQGMO_WAIT | CMQC.MQGMO_BROWSE_FIRST;
				MQMessage myMessage = new MQMessage();
				browseQueue.get(myMessage, gmo);
				System.out.println(" GET of message number: " + 1);
				System.out.println("****Message descriptor****\n");
				System.out.println("  StrucId  : 'MD  '" + "  Version : " + myMessage.getVersion());
				System.out.println("  Report   : " + myMessage.report + "  MsgType : " + myMessage.messageType);
				System.out.println("  Expiry   : " + myMessage.expiry + "  Feedback : " + myMessage.feedback);
				System.out
						.println("  Encoding : " + myMessage.encoding + "  CodedCharSetId : " + myMessage.characterSet);
				System.out.println("  Format : '" + myMessage.format + "'");
				System.out.println("  Priority : " + myMessage.priority + "  Persistence : " + myMessage.persistence);
				System.out.print("  MsgId : ");
				dumpHexId(myMessage.messageId);
				System.out.print("  CorrelId : ");
				dumpHexId(myMessage.correlationId);
				System.out.println("  BackoutCount : " + myMessage.backoutCount);
				System.out.println("  ReplyToQ     : '" + myMessage.replyToQueueName + "'");
				System.out.println("  ReplyToQMgr  : '" + myMessage.replyToQueueManagerName + "'");

				System.out.println("  ** Identity Context");
				System.out.println("  UserIdentifier : '" + myMessage.userId + "'");
				System.out.println("  Accounting Token :");
				System.out.print("   ");
				dumpHexId(myMessage.accountingToken);
				System.out.println("  ApplIdentityData : '" + myMessage.applicationIdData + "'");

				System.out.println("  ** Origin Context");
				System.out.println("  PutApplType    : '" + myMessage.putApplicationType + "'");
				System.out.println("  PutApplName    : '" + myMessage.putApplicationName + "'");

				System.out.print("  PutDate  : '");
				System.out.print(myMessage.putDateTime.get(Calendar.YEAR));
				int myMonth = myMessage.putDateTime.get(Calendar.MONTH) + 1;
				if (myMonth < 10) {
					System.out.print("0");
				}
				System.out.print(myMonth);

				int myDay = myMessage.putDateTime.get(Calendar.DAY_OF_MONTH);
				if (myDay < 10) {
					System.out.print("0");
				}
				System.out.print(myDay);
				System.out.print("'    ");

				System.out.print("PutTime  : '");
				int myHour = myMessage.putDateTime.get(Calendar.HOUR_OF_DAY);
				if (myHour < 10) {
					System.out.print("0");
				}
				System.out.print(myHour);

				int myMinute = myMessage.putDateTime.get(Calendar.MINUTE);
				if (myMinute < 10) {
					System.out.print("0");
				}
				System.out.print(myMinute);

				int mySecond = myMessage.putDateTime.get(Calendar.SECOND);
				if (mySecond < 10) {
					System.out.print("0");
				}
				System.out.print(mySecond);

				int myMsec = myMessage.putDateTime.get(Calendar.MILLISECOND);
				myMsec = myMsec / 10;
				if (myMsec < 10) {
					System.out.print("0");
				}
				System.out.print(myMsec);
				System.out.println("'");

				System.out.println("  ApplOriginData : '" + myMessage.applicationOriginData + "'");
				System.out.println();
				System.out.print("  GroupId : ");
				dumpHexId(myMessage.groupId);
				System.out.println("  MsgSeqNumber   : '" + myMessage.messageSequenceNumber + "'");
				System.out.println("  Offset         : '" + myMessage.offset + "'");
				System.out.println("  MsgFlags       : '" + myMessage.messageFlags + "'");
				System.out.println("  OriginalLength : '" + myMessage.originalLength + "'");
				System.out.println();

				System.out.println("****   Message     ****");
				System.out.println();
				System.out.println(" length - " + myMessage.getMessageLength() + " bytes\n");
				dumpHexMessage(myMessage);
				System.out.println();
				System.out.println();
				browseQueue.close();
				_qMgr.disconnect();
			}
		} catch (MQException ex) {
			System.out.println("MQ exception: CC = " + ex.completionCode + " RC = " + ex.reasonCode);
		} catch (EOFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @throws MQException
	 ***********************************************************/
	/*
	 * Qurey the current depth of the queue /
	 *************************************************************/
	private int getQueueCurrentDepth() throws MQException {
		int openOptions2 = CMQC.MQOO_INQUIRE | CMQC.MQGMO_WAIT | CMQC.MQGMO_CONVERT;

		MQQueue getQ = _qMgr.accessQueue(myQueue, openOptions2);
		System.out.println("Current Depth:" + getQ.getCurrentDepth());

		return getQ.getCurrentDepth();
	}

	/*************************************************************/
	/* This method will dump the text of the message in both hex */
	/* and character format. */
	/*************************************************************/
	static void dumpHexMessage(MQMessage myMsg) throws java.io.IOException {

		int DataLength = myMsg.getMessageLength();
		int ch = 0;
		int chars_this_line = 0;
		int CHARS_PER_LINE = 16;
		StringBuffer line_text = new StringBuffer();
		line_text.setLength(0);
		do {
			chars_this_line = 0;
			String myPos = new String(Integer.toHexString(ch));
			for (int i = 0; i < 8 - myPos.length(); i++) {
				System.out.print("0");
			}
			System.out.print((String) (Integer.toHexString(ch)).toUpperCase() + ": ");

			while ((chars_this_line < CHARS_PER_LINE) && (ch < DataLength)) {

				if (chars_this_line % 2 == 0) {
					System.out.print(" ");
				}
				char b = (char) (myMsg.readUnsignedByte() & 0xFF);
				if (b < 0x10) {
					System.out.print("0");
				}

				System.out.print((String) (Integer.toHexString(b)).toUpperCase());

				/***********************************************/
				/* If this is a printable character, print it. */
				/* Otherwise, print a '.' as a placeholder. */
				/***********************************************/
				if (Character.isLetterOrDigit(b) || Character.getType(b) == Character.CONNECTOR_PUNCTUATION
						|| Character.getType(b) == Character.CURRENCY_SYMBOL
						|| Character.getType(b) == Character.MATH_SYMBOL
						|| Character.getType(b) == Character.MODIFIER_SYMBOL
						|| Character.getType(b) == Character.UPPERCASE_LETTER
						|| Character.getType(b) == Character.SPACE_SEPARATOR
						|| Character.getType(b) == Character.DASH_PUNCTUATION
						|| Character.getType(b) == Character.START_PUNCTUATION
						|| Character.getType(b) == Character.END_PUNCTUATION
						|| Character.getType(b) == Character.OTHER_PUNCTUATION) {
					line_text.append(b);
				} else {
					line_text.append('.');
				}
				chars_this_line++;
				ch++;
			}

			/*****************************************************/
			/* pad with blanks to format the last line correctly */
			/*****************************************************/
			if (chars_this_line < CHARS_PER_LINE) {
				for (; chars_this_line < CHARS_PER_LINE; chars_this_line++) {
					if (chars_this_line % 2 == 0)
						System.out.print(" ");
					System.out.print("  ");
					line_text.append(' ');
				}
			}

			System.out.println(" '" + line_text.toString() + "'");
			line_text.setLength(0);
		} while (ch < DataLength);

	} /* end of dumpHexMessage */

	/****************************************************/
	/* Some of the MQ Ids are actually byte strings and */
	/* need to be dumped in hex format. */
	/****************************************************/
	static void dumpHexId(byte[] myId) {
		System.out.print("X'");
		for (int i = 0; i < myId.length; i++) {
			char b = (char) (myId[i] & 0xFF);
			if (b < 0x10) {
				System.out.print("0");
			}
			System.out.print((String) (Integer.toHexString(b)).toUpperCase());
		}
		System.out.println("'");
	}
}
