package crs.fcl.integration.iib;

public class Worktree {
	private String worktreeName;
	private String remoteBranch;
	private String localBranch;
	private String localPath;
	public String getWorktreeName() {
		return worktreeName;
	}
	public void setWorktreeName(String worktreeName) {
		this.worktreeName = worktreeName;
	}
	public String getRemoteBranch() {
		return remoteBranch;
	}
	public void setRemoteBranch(String remoteBranch) {
		this.remoteBranch = remoteBranch;
	}
	public String getLocalBranch() {
		return localBranch;
	}
	public void setLocalBranch(String localBranch) {
		this.localBranch = localBranch;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
}
