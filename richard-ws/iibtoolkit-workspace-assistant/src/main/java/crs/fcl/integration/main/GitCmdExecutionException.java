package crs.fcl.integration.main;

public class GitCmdExecutionException extends Exception{

	/**
	 * Exception from running GIT commands
	 */
	private static final long serialVersionUID = 1L;

	public GitCmdExecutionException(String errorMessage) {
		super(errorMessage);
	}

}
