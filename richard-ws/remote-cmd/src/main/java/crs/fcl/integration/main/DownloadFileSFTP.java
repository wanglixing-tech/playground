package crs.fcl.integration.main;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
/**
 * 
 * @author javagists.com
 *
 */
public class DownloadFileSFTP {
 
    public static void main(String[] args) throws Exception {
 
 JSch jsch = new JSch();
        Session session = null;
        try {
           session = jsch.getSession("rwang", "127.0.0.1", 2202);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword("Stamina168");
            session.connect();
            
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.get("/tmp/test.txt", "C:\\Temp\\testDownload.txt");  
            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();  
        } catch (SftpException e) {
            e.printStackTrace();
        }
 
   }
 
}