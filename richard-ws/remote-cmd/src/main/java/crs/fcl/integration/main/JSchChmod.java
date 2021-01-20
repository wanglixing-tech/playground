package crs.fcl.integration.main;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class JSchChmod {

	@Test
	public void testchmod() throws JSchException, SftpException, IOException {
		JSch jsch = new JSch();
		Session session = null;
		session = jsch.getSession("rwang", "127.0.0.1", 2202);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setPassword("Stamina168");
		session.connect();
		Date dateStart = new Date();
		chmod("/tmp/testUpload.pom", "777", session);
		Date dateEnd = new Date();
		session.disconnect();
		System.out.println(dateEnd.getTime() - dateStart.getTime() + "ms");
	}

	/**
	 * 
	 * @param fileLinux
	 * @param chmod Octal String
	 * @param session
	 * @throws JSchException
	 * @throws SftpException
	 */
	public static void chmod(String fileLinux, String chmod, Session session) throws JSchException, SftpException {
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
		channel.connect();
		int chmodInt = Integer.parseInt(chmod, 8);
		channel.chmod(chmodInt, fileLinux);
		channel.disconnect();

	}
}