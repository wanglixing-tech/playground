package crs.fcl.esb.mq;

import java.io.EOFException;

/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2008,2012" 
 *   crc="3015388427" > 
 *   Licensed Materials - Property of IBM  
 *    
 *   5724-H72,5655-R36,5655-L82,5724-L26, 
 *    
 *   (C) Copyright IBM Corp. 2008, 2012 All Rights Reserved.  
 *    
 *   US Government Users Restricted Rights - Use, duplication or  
 *   disclosure restricted by GSA ADP Schedule Contract with  
 *   IBM Corp.  
 *   </copyright> 
 */

import java.io.IOException;
import java.util.Calendar;

import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFException;
import com.ibm.mq.headers.pcf.PCFMessage;

public class MQBrowse3 {

	/** The SCCSID which is expanded when the file is extracted from CMVC */
	public static final String sccsid = "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=MQJavaSamples/pcf/PCF_ListQueueNames.java"; //$NON-NLS-1$
	public static String[] qNameList;
	private static MQQueueManager _qMgr = null;

	/**
	 * PCF sample entry function. When calling this sample, use either of the
	 * following formats:-
	 * <p>
	 * <table border="1">
	 * <tr>
	 * <td>Format</td>
	 * <td>Example</td>
	 * <td>Information</td>
	 * </tr>
	 * <tr>
	 * <td>PCF_Sample QueueManager</td>
	 * <td>PCF_Sample QM</td>
	 * <td>Use this prototype when connecting to a local queue manager.</td>
	 * </tr>
	 * <tr>
	 * <td>PCF_Sample QueueManager Host Port</td>
	 * <td>PCF_Sample QM localhost 1414</td>
	 * <td>Use this prototype when connecting to a queue manager using client
	 * bindings.</td>
	 * </tr>
	 * </table>
	 * 
	 * @param args
	 *            Input parameters.
	 */
	public static void main(String[] args) {
		PCF_CommonMethods pcfCM = new PCF_CommonMethods();

		try {
			if (pcfCM.ParseParameters(args)) {
				pcfCM.CreateAgent(args.length);

				String qMgrName = args[0];
				qNameList = ListQueueNames(pcfCM);

				for (int index = 0; index < qNameList.length; index++) {
					String qName = qNameList[index];
					System.out.println("Queue Name:" + qName);
					browseOneMsg(qMgrName, qName);
				}

				pcfCM.DestroyAgent();
			}
		} catch (Exception e) {
			pcfCM.DisplayException(e);
		}
		return;
	}

	/**
	 * ListQueueNames uses the PCF command 'MQCMD_INQUIRE_Q_NAMES' to gather
	 * information about all Queues (using the '*' wildcard to denote 'all queues')
	 * contained within the given Queue Manager. The information is displayed in a
	 * tabular form on the console.<br>
	 * For more information on the Inquire Queue Names command, please read the
	 * "Programmable Command Formats and Administration Interface" section within
	 * the Websphere MQ documentation.
	 * 
	 * @param pcfCM
	 *            Object used to hold common objects used by the PCF samples.
	 * @throws PCFException
	 * @throws IOException
	 * @throws MQDataException
	 */
	static String[] ListQueueNames(PCF_CommonMethods pcfCM) {
		String[] names = null;
		// Create the PCF message type for the inquire.
		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q_NAMES);

		// Add the inquire rules.
		// Queue name = wildcard.
		pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, "*");

		// Queue type = ALL.
		pcfCmd.addParameter(MQConstants.MQIA_Q_TYPE, MQConstants.MQQT_LOCAL);

		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse;

		try {
			pcfResponse = pcfCM.agent.send(pcfCmd);
			names = (String[]) pcfResponse[0].getParameterValue(MQConstants.MQCACF_Q_NAMES);
		} catch (PCFException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (MQDataException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		return names;
	}

	static void browseOneMsg(String qMgrName, String qName) {
		try {
			_qMgr = new MQQueueManager(qMgrName);

			if (getQueueCurrentDepth(qName) != 0) {
				/******************************************************/
				/* Set up our options to browse for the first message */
				/******************************************************/
				int openOptions = CMQC.MQOO_BROWSE + CMQC.MQOO_FAIL_IF_QUIESCING;
				MQQueue browseQueue = null;

				browseQueue = _qMgr.accessQueue(qName, openOptions, null, null, null);
				System.out.println("\n OPEN - '" + qName + "'\n\n");

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
			System.out.println("MQ exception: CC=" + ex.completionCode + " RC=" + ex.reasonCode);
			if (ex.reasonCode == 2042) {
				System.out.println("This queue object can't be accessed because of MQRC_OBJECT_IN_USE.");
			} else if (ex.reasonCode == 2035) {
				System.out.println("This queue object can't be accessed because of MQRC_NOT_AUTHORIZED.");
			} else if (ex.reasonCode == 2101) {
				System.out.println("!!!!! This queue object can't be accessed because of MQRC_OBJECT_DAMAGED,");
				System.out.println("Please take other actions to get it recovered !!!!!");
			}
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
	static int getQueueCurrentDepth(String qName) throws MQException {
		int openOptions2 = CMQC.MQOO_INQUIRE | CMQC.MQGMO_WAIT | CMQC.MQGMO_CONVERT;

		MQQueue getQ = _qMgr.accessQueue(qName, openOptions2);
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
