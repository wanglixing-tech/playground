package crs.fcl.integration.main;

public class SshExecResponse {
	String stdOut = null;
	String stdErr = null;
	int execCode = -1;
	public String getStdOut() {
		return stdOut;
	}
	public void setStdOut(String stdOut) {
		this.stdOut = stdOut;
	}
	public String getStdErr() {
		return stdErr;
	}
	public void setStdErr(String stdErr) {
		this.stdErr = stdErr;
	}
	public int getExecCode() {
		return execCode;
	}
	public void setExecCode(int execCode) {
		this.execCode = execCode;
	}

}
