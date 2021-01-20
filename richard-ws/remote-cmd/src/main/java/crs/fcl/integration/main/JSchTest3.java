package crs.fcl.integration.main;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class JSchTest3 {
	static String host = "127.0.0.1";
	static String user = "rwang";
	static String password = "Stamina168";
	static String command = "ls -l\n";
	public static void main(String[] args) throws Exception {
		try {
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, 2202);
			session.setUserInfo(new SSHUserInfo(user, password));
			session.connect();
			Channel channel = session.openChannel("exec");
			channel.setInputStream(new ByteArrayInputStream(command.getBytes(StandardCharsets.UTF_8)));
			channel.setOutputStream(System.out);
			InputStream in = channel.getInputStream();
			StringBuilder outBuff = new StringBuilder();
			int exitStatus = -1;

			channel.connect();

			while (true) {
				for (int c; ((c = in.read()) >= 0);) {
					outBuff.append((char) c);
				}

				if (channel.isClosed()) {
					if (in.available() > 0)
						continue;
					exitStatus = channel.getExitStatus();
					break;
				}
			}
			channel.disconnect();
			session.disconnect();

			// print the buffer's contents
			System.out.print(outBuff.toString());
			// print exit status
			System.out.print("Exit status of the execution: " + exitStatus);
			if (exitStatus == 0) {
				System.out.print(" (OK)\n");
			} else {
				System.out.print(" (NOK)\n");
			}

		} catch (IOException | JSchException ioEx) {
			System.err.println(ioEx.toString());
		}
		
		remoteExec("ls -l abc");
	}
	
    private static ExecResponse remoteExec(String command)throws Exception
    {
        System.out.println( "doing remote execution: " + command);
        ExecResponse results = new ExecResponse();
        try {
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, 2202);
			session.setUserInfo(new SSHUserInfo(user, password));
			session.connect();
            Channel channel = session.openChannel("exec");
            /*
             * Following lines means we use setCommand() instead of setInputStream() as command's input.
             * We can use: 
             * channel.setInputStream(new ByteArrayInputStream(command.getBytes(StandardCharsets.UTF_8)));
			 * We can also use (case th echannel to ChannelExec):
			 * ((ChannelExec)channel).setCommand(command);
			 * 
			 * Get stdErr with channel.getExtInputStream() instead of  ((ChannelExec)channel).getErrStream();
			 * We can use:
			 * InputStream stderr = channel.getExtInputStream(); 
			 * We can also use (case th echannel to ChannelExec):
			 * InputStream err = ((ChannelExec)channel).getErrStream();
			 *
             */
            channel.setInputStream(null);
            ((ChannelExec)channel).setCommand(command);
 
            StringBuilder outputBuffer = new StringBuilder();
            StringBuilder errorBuffer = new StringBuilder();

            InputStream stdout = channel.getInputStream();
            InputStream stderr = channel.getExtInputStream();

            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (stdout.available() > 0) {
                    int i = stdout.read(tmp, 0, 1024);
                    if (i < 0) break;
                    outputBuffer.append(new String(tmp, 0, i));
                }
                while (stderr.available() > 0) {
                    int i = stderr.read(tmp, 0, 1024);
                    if (i < 0) break;
                    errorBuffer.append(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    if ((stdout.available() > 0) || (stderr.available() > 0)) continue; 
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try { 
                  Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            
            System.out.println("exit-status: " + channel.getExitStatus());
            results.setExecCode(channel.getExitStatus());
            System.out.println("output: " + outputBuffer.toString());
            results.setStdOut(outputBuffer.toString());
            System.out.println("error: " + errorBuffer.toString());
            results.setStdErr(errorBuffer.toString());
            
            channel.disconnect();
			session.disconnect();

            // Get command's output
            //InputStream commandOutput = channel.getExtInputStream();
 
            // Direct stderr output of command
            //InputStream err = ((ChannelExec)channel).getErrStream();
            //InputStreamReader errStrRdr = new InputStreamReader(err, "UTF8");
            //Reader errStrBufRdr = new BufferedReader(errStrRdr);

            // Direct stdout output of command
            //InputStream out = channel.getInputStream();
            //InputStreamReader outStrRdr = new InputStreamReader(out, "UTF8");
            //Reader outStrBufRdr = new BufferedReader(outStrRdr);

            //while(true)
            //{
            //    if(channel.isClosed()) {
            //        results.setExecCode(channel.getExitStatus());
            //        break;
            //    }
            //    try{
            //        Thread.sleep(1000);
            //    } catch(InterruptedException ie) { }
            // }
            
            //channel.disconnect();

           // int ch;
            //StringBuffer stdout = new StringBuffer();
            //while ((ch = outStrBufRdr.read()) > -1) {
            //    stdout.append((char)ch);
            //}
            //results.setStdOut(stdout.toString());
            
            //StringBuffer stderr = new StringBuffer();
            //while ((ch = errStrBufRdr.read()) > -1) {
            //    stderr.append((char)ch);
            //}
            //results.setStdErr(stderr.toString());
            
        } catch (Exception e) {
            throw new Exception("Error executing remote command '" + command + "'", e);
        }
        return results;
    }

}
