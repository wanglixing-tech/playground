package crs.fcl.integration.main;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Map;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class IIBSshExecHelper {

	private JSch jSch = new JSch();
	private Session session;
	private IIBBroker in = null;
	public IIBSshExecHelper() {
	}

	public IIBSshExecHelper(IIBBroker in) {
		this.in = in;
	}

	public void openSession() throws JSchException, URISyntaxException {
		open(in.getHostName(), in.getPort(), in.getProxyUser());
	}

	public void open(String hostname, int port, String username) throws JSchException, URISyntaxException {
		//Use key authentication if it is set, else use password auth
		if (in.getSshKeyName() != null && in.getSshKeyName() != ""){
			File keyFile = new File(getDefaultHomeDir() + in.getSshKeyName());
			if (! keyFile.exists()) {
				System.out.println("Specified key file does not exist... ");
				System.exit(999);
			} else {
				jSch.addIdentity(keyFile.getAbsolutePath());
			}
			session = jSch.getSession(username, hostname, port);
			session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
		} else if (in.getPassword() != null && in.getPassword() != "") {
			session = jSch.getSession(username, hostname, port);
			session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setTimeout(15000);
			session.setPassword(in.getPassword());
		} 
		session.connect();
	}

	public void closeSession() {
		session.disconnect();
		//System.out.println("Disconnected channel and session");
	}

	public SshExecResponse runCommand(String command) throws JSchException, IOException, URISyntaxException {
        SshExecResponse results = new SshExecResponse();

        openSession();
		//if (!session.isConnected())
		//	throw new RuntimeException("Not connected to an open session.  Call open() first!");

		ChannelExec channel = null;
		channel = (ChannelExec) session.openChannel("exec");

		channel.setCommand(command);
		channel.setInputStream(null);

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
                //System.out.println("exit-status: " + channel.getExitStatus());
                break;
            }
            try { 
              Thread.sleep(1000);
            } catch (Exception ee) {
            }
        }
        //System.out.println("exit-status: " + channel.getExitStatus());
        results.setExecCode(channel.getExitStatus());
        //System.out.println("output: " + outputBuffer.toString());
        results.setStdOut(outputBuffer.toString());
        //System.out.println("error: " + errorBuffer.toString());
        results.setStdErr(errorBuffer.toString());
        
		channel.disconnect();
		closeSession();
		return results;
	}


	public void download(String fileName, String fromPath, boolean isUserHomeBased) throws Exception {
        SshExecResponse results = new SshExecResponse();
		openSession();
		//if (!session.isConnected())
		//	throw new RuntimeException("Not connected to an open session.  Call open() first!");

		ChannelSftp channel = (ChannelSftp) this.session.openChannel("sftp");
		channel.setInputStream(null);

		channel.connect();
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		BufferedOutputStream buff = new BufferedOutputStream(outputStream);
		String absolutePath = isUserHomeBased ? channel.getHome() + "/" + fromPath : fromPath;
		channel.cd(absolutePath);
		channel.get(fileName, buff);

		channel.disconnect();
		System.out.println("Finished sftp commands!");
		closeSession();
		return;
	}

	public void upload(File sourceFile, String toPath, String filePerm) throws Exception {
        if (!toPath.endsWith("/")) toPath = toPath.concat("/");
        if (toPath.startsWith("/")) toPath = toPath.substring(1);

		openSession();
		//if (!session.isConnected())
		//	throw new RuntimeException("Not connected to an open session.  Call open() first!");

		ChannelSftp channel = (ChannelSftp) this.session.openChannel("sftp");
		channel.setInputStream(null);

		channel.connect();
		channel.cd(toPath);
		channel.put(sourceFile.getAbsolutePath(), sourceFile.getName(), ChannelSftp.OVERWRITE);
		if (filePerm != null) {
			channel.chmod(Integer.parseInt(filePerm, 8), toPath + "/" + sourceFile.getName());
		}

		channel.disconnect();
		System.out.println("Finished sftp commands!");
		closeSession();
		return;
	}

    public void scpRemoteToLocal(String from, String to, String fileName) throws JSchException, IOException, URISyntaxException {
        from = from + File.separator + fileName;
        String prefix = null;

        if (new File(to).isDirectory()) {
            prefix = to + File.separator;
        }
		openSession();
        // exec 'scp -f rfile' remotely
        String command = "scp -f " + from;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        // get I/O streams for remote scp
        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();

        channel.connect();

        byte[] buf = new byte[1024];
        
        // send '\0'
        buf[0] = 0;
        out.write(buf, 0, 1);
        out.flush();

        while (true) {
            int c = checkAck(in);
            if (c != 'C') {
                break;
            }

            // read '0644 '
            in.read(buf, 0, 5);

            long filesize = 0L;
            while (true) {
                if (in.read(buf, 0, 1) < 0) {
                    // error
                    break;
                }
                if (buf[0] == ' ') break;
                filesize = filesize * 10L + (long) (buf[0] - '0');
            }

            String file = null;
            for (int i = 0; ; i++) {
                in.read(buf, i, 1);
                if (buf[i] == (byte) 0x0a) {
                    file = new String(buf, 0, i);
                    break;
                }
            }

            System.out.println("file-size=" + filesize + ", file=" + file);

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            // read a content of lfile
            FileOutputStream fos = new FileOutputStream(prefix == null ? to : prefix + file);
            int foo;
            while (true) {
                if (buf.length < filesize) foo = buf.length;
                else foo = (int) filesize;
                foo = in.read(buf, 0, foo);
                if (foo < 0) {
                    // error
                    break;
                }
                fos.write(buf, 0, foo);
                filesize -= foo;
                if (filesize == 0L) break;
            }

            if (checkAck(in) != 0) {
                System.exit(0);
            }

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            try {
                if (fos != null) fos.close();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }

        channel.disconnect();
        closeSession();
    }
    
    public void scpLocalToRemote(File fullPath2localFile, String remoteDir, String remoteFile) throws JSchException, IOException, URISyntaxException {
        boolean ptimestamp = true;
		openSession();
		/**
		 * Create exec Channel with command 'scp -t rFile ' remotely
		 */
		remoteFile = remoteDir + "/" + remoteFile; 
		remoteFile = remoteFile.replace("'", "'\"'\"'");
		remoteFile = "'" + remoteFile + "'";
		System.out.println("Remote file name=" + remoteFile);
		String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + remoteFile;
		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);

		// get I/O streams for remote scp
		OutputStream out = channel.getOutputStream();
		InputStream in = channel.getInputStream();

		channel.connect();

		if (checkAck(in) != 0) {
			System.exit(0);
		}

		//File _lfile = new File(fullPath2localFile);

		if (ptimestamp) {
			command = "T" + (fullPath2localFile.lastModified() / 1000) + " 0";
			// The access time should be sent here,
			// but it is not accessible with JavaAPI ;-<
			command += (" " + (fullPath2localFile.lastModified() / 1000) + " 0\n");
			out.write(command.getBytes());
			out.flush();
			if (checkAck(in) != 0) {
				System.exit(0);
			}
		}
		
		String localFilePath = fullPath2localFile.getAbsolutePath().toString();
		// send "C0644 filesize filename", where filename should not include '/'
		long filesize = fullPath2localFile.length();
		command = "C0644 " + filesize + " ";
		if (localFilePath.lastIndexOf('/') > 0) {
			command += localFilePath.substring(localFilePath.lastIndexOf('/') + 1);
		} else {
			command += localFilePath;
		}
		command += "\n";
		out.write(command.getBytes());
		out.flush();
		if (checkAck(in) != 0) {
			System.exit(0);
		}

		// send a content of localFile
		FileInputStream fis = new FileInputStream(fullPath2localFile);
		byte[] buf = new byte[1024];
		while (true) {
			int len = fis.read(buf, 0, buf.length);
			if (len <= 0)
				break;
			out.write(buf, 0, len); // out.flush();
		}
		fis.close();
		fis = null;
		// send '\0'
		buf[0] = 0;
		out.write(buf, 0, 1);
		out.flush();
		if (checkAck(in) != 0) {
			System.exit(0);
		}
		out.close();

        channel.disconnect();
        closeSession();
    }

    public static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //         -1
        // System.out.println("returnCode=" + b);
        if (b == 0) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }

    public static void main(String[] args) throws JSchException, IOException, URISyntaxException {
	}
    
	public static String getDefaultHomeDir() {
		String homePath = null;
		Map<String, String> env = System.getenv();
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		if (isWindows) {
			homePath  =env.get("USERPROFILE");
		} else {
			homePath = System.getProperty("user.home");
		}
		return new String(homePath + File.separator + ".ssh" + File.separator);
	}


}