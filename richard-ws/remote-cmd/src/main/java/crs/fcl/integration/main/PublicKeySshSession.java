package crs.fcl.integration.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.Validate;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class PublicKeySshSession {
	
	final Session session;
	
	public PublicKeySshSession(final Builder builder) {
		this.session = builder.jschSession;
	}

	public void execute(String command) throws IOException {
		if (session == null) {
			throw new IllegalArgumentException("Session object is null.");
		}
		
		if (command != null && command.isEmpty()) {
			throw new IllegalArgumentException("SSH command is blank.");
		}
		
		try {
			
			session.connect();
			
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			//((ChannelExec) channel).setPty(false);
			
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err, true);
			InputStream input = channel.getInputStream();
			channel.connect();
			System.out.println("Channel Connected to server with command: " + command);
			InputStreamReader inputReader = new InputStreamReader(input);
			BufferedReader bufferedReader = new BufferedReader(inputReader);
			String line = null;
			System.out.println("Result from inputStream:");
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
			}
			bufferedReader.close();
			inputReader.close();
			channel.disconnect();
			session.disconnect();
			
		} catch (JSchException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static class Builder {
		private String host;
		private String username;
		private int port;
		private Path privateKeyPath;
		private com.jcraft.jsch.Logger logger;
		
		private Session jschSession;

		public Builder(String host, String username, int port, String path) {
			this.host = Validate.notBlank(host);
			this.username = Validate.notBlank(username);
			this.port = port;
			this.privateKeyPath = Paths.get(path);
		}
		
		private void validate() {
			if (port < 2) {
				throw new IllegalArgumentException("Port number must start with 2.");
			}
		}

		public PublicKeySshSession build() {
			validate();

			if (logger != null) {
				JSch.setLogger(new JschLogger());
			}

			JSch jsch = new JSch();

			Session session = null;

			try {

				jsch.addIdentity(privateKeyPath.toString());

				session = jsch.getSession(username, host, port);
				session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");

				java.util.Properties config = new java.util.Properties();
				config.put("StrictHostKeyChecking", "no");

				session.setConfig(config);

			} catch (JSchException e) {
				throw new RuntimeException("Failed to create Jsch Session object.", e);
			}
			
			this.jschSession = session;

			return new PublicKeySshSession(this);
		}

		public Builder logger(com.jcraft.jsch.Logger logger) {
			this.logger = logger;
			return this;
		}

	}

}