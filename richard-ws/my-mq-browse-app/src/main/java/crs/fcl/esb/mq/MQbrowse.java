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
import java.util.Hashtable;
//import java.util.logging.Logger;

public class MQbrowse {
	//private Logger logger = Logger.getLogger(MQbrowse.class.getName());
	private Hashtable<String, String> params = null;
	private Hashtable<String, Object> mqht = null;
	private static String myQmgr = null;
	private static String myQueue = null;

	public static void main(String args[]) throws IOException {
		MQbrowse mqBrowse = new MQbrowse();
		try {
			mqBrowse.init(args);
			mqBrowse.start();
		} catch (IllegalArgumentException e) {
			System.out.println("Usage: java MQBrowse -h host -p port -c channel -m QueueManagerName -q QueueName");
			System.exit(1);
		}

		System.exit(0);
	}

	/**
	 * Make sure the required parameters are present.
	 * 
	 * @return true/false
	 */
	private boolean allParamsPresent() {
		boolean b = params.containsKey("-h") && params.containsKey("-p") && params.containsKey("-c")
				&& params.containsKey("-m") && params.containsKey("-q");
		if (b) {
			try {
				Integer.parseInt((String) params.get("-p"));
			} catch (NumberFormatException e) {
				b = false;
			}
		}

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

			mqht = new Hashtable<String, Object>();

			mqht.put(CMQC.CHANNEL_PROPERTY, params.get("-c"));
			mqht.put(CMQC.HOST_NAME_PROPERTY, params.get("-h"));
			mqht.put(CMQC.USER_ID_PROPERTY, "mqm");
			mqht.put(CMQC.PASSWORD_PROPERTY, "Stamina168");
			

			try {
				mqht.put(CMQC.PORT_PROPERTY, new Integer(params.get("-p")));
			} catch (NumberFormatException e) {
				mqht.put(CMQC.PORT_PROPERTY, new Integer(1414));
			}

			// I don't want to see MQ exceptions at the console.
			MQException.log = null;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void start() {
		MQQueueManager _qMgr = null;
		int openOptions = CMQC.MQOO_BROWSE + CMQC.MQOO_FAIL_IF_QUIESCING;

		try {
			_qMgr = new MQQueueManager(myQmgr, mqht);
			MQQueue browseQueue = _qMgr.accessQueue(myQueue, openOptions, null, null, null);
			System.out.println("\n OPEN - '" + myQueue + "'\n\n");
			/******************************************************/
			/* Set up our options to browse for the first message */
			/******************************************************/
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.options = CMQC.MQGMO_WAIT | CMQC.MQGMO_BROWSE_FIRST;
			MQMessage myMessage = new MQMessage();
			browseQueue.get(myMessage, gmo);
			String msg = myMessage.readStringOfCharLength(myMessage.getMessageLength());
			System.out.println("Browsed message: " + msg);
			browseQueue.close();
			_qMgr.disconnect();
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

}
