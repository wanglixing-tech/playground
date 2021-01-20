package crs.fcl.integration.main;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SSHConnectionManager {

	private Session session;

	private String username = "rwang";
	private String password = "Stamina168";
	private String hostname = "127.0.0.1";

	public SSHConnectionManager() {
	}

	public SSHConnectionManager(String hostname, String username, String password) {
		this.hostname = hostname;
		this.username = username;
		this.password = password;
	}

	public void open() throws JSchException {
		open(this.hostname, this.username, this.password);
	}

	public void open(String hostname, String username, String password) throws JSchException {

		JSch jSch = new JSch();

		session = jSch.getSession(username, hostname, 2202);
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no"); // not recommended
		session.setConfig(config);
		session.setPassword(password);

		System.out.println("Connecting SSH to " + hostname + " - Please wait for few seconds... ");
		session.connect();
		System.out.println("Connected!");
	}

	public String runCommand(String command) throws JSchException, IOException {

		String ret = "";

		if (!session.isConnected())
			throw new RuntimeException("Not connected to an open session.  Call open() first!");

		ChannelExec channel = null;
		channel = (ChannelExec) session.openChannel("exec");

		channel.setCommand(command);
		channel.setInputStream(null);

		// PrintStream out = new PrintStream(channel.getOutputStream());
		InputStream in = channel.getInputStream(); // channel.getInputStream();

		channel.connect();

		// you can also send input to your running process like so:
		// String someInputToProcess = "something";
		// out.println(someInputToProcess);
		// out.flush();

		ret = getChannelOutput(channel, in);

		channel.disconnect();

		System.out.println("Finished sending commands!");

		return ret;
	}

	private String getChannelOutput(Channel channel, InputStream in) throws IOException {

		byte[] buffer = new byte[1024];
		StringBuilder strBuilder = new StringBuilder();

		String line = "";
		while (true) {
			while (in.available() > 0) {
				int i = in.read(buffer, 0, 1024);
				if (i < 0) {
					break;
				}
				strBuilder.append(new String(buffer, 0, i));
				line = new String(buffer, 0, i);
			}

			if (line.contains("logout")) {
				break;
			}

			if (channel.isClosed()) {
				break;
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException  ee) {
				break;
			}
		}

		return strBuilder.toString();
	}

	public void close() {
		session.disconnect();
		System.out.println("Disconnected channel and session");
	}

	public String download(String fileName, String fromPath, boolean isUserHomeBased) throws Exception {
		String ret = "";

		if (!session.isConnected())
			throw new RuntimeException("Not connected to an open session.  Call open() first!");

		ChannelSftp channel = (ChannelSftp) this.session.openChannel("sftp");
		channel.setInputStream(null);

		// THis is for getting standard output and error yield while running sftp
		InputStream in = channel.getInputStream();

		channel.connect();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		BufferedOutputStream buff = new BufferedOutputStream(outputStream);
		String absolutePath = isUserHomeBased ? channel.getHome() + "/" + fromPath : fromPath;
		channel.cd(absolutePath);
		channel.get(fileName, buff);

		ret = getChannelOutput(channel, in);

		channel.disconnect();

		System.out.println("Finished sftp commands!");

		return ret;
	}

	public String upload(File sourceFile, String toPath, String filePerm) throws Exception {
		String ret = "";

		if (!session.isConnected())
			throw new RuntimeException("Not connected to an open session.  Call open() first!");

		ChannelSftp channel = (ChannelSftp) this.session.openChannel("sftp");
		channel.setInputStream(null);

		// This is for getting standard output and error yield while running sftp
		//InputStream in = channel.getInputStream();

		channel.connect();
		channel.cd(toPath);
		channel.put(sourceFile.getAbsolutePath(), sourceFile.getName(), ChannelSftp.OVERWRITE);
		if (filePerm != null) {
			channel.chmod(Integer.parseInt(filePerm, 8), toPath + "/" + sourceFile.getName());
		}

		// Following statement will wait forever
		//ret = getChannelOutput(channel, in);

		channel.disconnect();

		System.out.println("Finished sftp commands!");

		return ret;
	}

	public static void main(String[] args) {

		SSHConnectionManager ssh = new SSHConnectionManager();
		try {
			ssh.open();

			// Run command on remote server
			// String ret = ssh.runCommand("ls -l && logout");
			// System.out.println(ret);

			// Download file from remote server
			// String ret1 = ssh.download("testUpload.pom", "/tmp", false);
			// System.out.println(ret1);

			// Upload file from local to remote server
			String toPath = "/tmp/rwang";
			String ret = ssh.runCommand("mkdir -p " + toPath);
			System.out.println(ret);
			
			String ret2 = ssh.upload(new File("C:\\Temp\\test.pom"), toPath, "777");
			System.out.println(ret2);

			ssh.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}