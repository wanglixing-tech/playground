package crs.fcl.integration.main;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JSchTest2 {
	public static void main(String[] args) {
		try {
			String command = "ls -la";
			String host = "127.0.0.1";
			int port = 2202;
			String user = "rwang";
			String password = "Stamina168";

			JSch jsch = new JSch();
			Session session;
			session = jsch.getSession(user, host, port);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setOutputStream(System.out, true);
			channelExec.setErrStream(System.err, true);
			channelExec.setCommand(command);
			channelExec.connect();

			channelExec.disconnect();
			session.disconnect();

		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void fileFetch(String host, String user, String keyLocation, String sourceDir, String destDir) {
		JSch jsch = new JSch();
		Session session = null;
		try {
			// set up session
			session = jsch.getSession(user, host);
			// use private key instead of username/password
			session.setConfig("PreferredAuthentications", "publickey,gssapi-with-mic,keyboard-interactive,password");
			jsch.addIdentity(keyLocation);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();

			// copy remote log file to localhost.
			ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();
			channelSftp.get(sourceDir, destDir);
			channelSftp.exit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
	}

	public static String connectAndExecute(String user, String host, String password, String command1) {
		String CommandOutput = null;
		try {

			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();

			Session session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig(config);
			session.connect();
			// System.out.println("Connected");

			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command1);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();

			channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);

					if (i < 0)
						break;
					// System.out.print(new String(tmp, 0, i));
					CommandOutput = new String(tmp, 0, i);
				}

				if (channel.isClosed()) {
					// System.out.println("exit-status: " +
					// channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
			// System.out.println("DONE");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CommandOutput;

	}

	public static Session establishWithKey(String sshHost, int sshPort, String user, String keyFilePath)
			throws JSchException {
		File keyFile = new File(keyFilePath);
		if (!keyFile.exists()) {
			String errorMsg = "Could not find SSH public key file in path: " + keyFilePath;
			System.out.println(errorMsg);
			throw new JSchException(errorMsg);
		}
		Session session;
		JSch jsch = new JSch();
		try {
			jsch.addIdentity(keyFile.getAbsolutePath());
			session = jsch.getSession(user, sshHost, sshPort);
		} catch (JSchException e) {
			System.out.println("SSH connection attempt to host: " + sshHost + ":" + sshPort + " failed");
			throw e;
		}
		return session;
	}

	public void deleteFile(String host, String user, String pwd, String remoteFile) {
		try {
			JSch ssh = new JSch();
			Session session = ssh.getSession(user, host, 22);

			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(pwd);

			session.connect();
			Channel channel = session.openChannel("exec");
			channel.connect();

			String command = "rm -rf " + remoteFile;
			System.out.println("command: " + command);
			// ((ChannelExec) channel).setCommand(command);

			channel.disconnect();
			session.disconnect();
		} catch (JSchException e) {
			System.out.println(e.getMessage().toString());
			e.printStackTrace();
		}
	}
}
