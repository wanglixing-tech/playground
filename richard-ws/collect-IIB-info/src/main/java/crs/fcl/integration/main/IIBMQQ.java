package crs.fcl.integration.main;

public class IIBMQQ {
	private String hostname;
	private String queuemanager;
	private String queue;
	private int curdepth;
	private int maxdepth;
	private int ipprocs;

	public final String getHostname() {
		return hostname;
	}

	public IIBMQQ setHostname(String hostname) {
		this.hostname = hostname;
		return this;
	}

	public final String getQueuemanager() {
		return queuemanager;
	}

	public IIBMQQ setQueuemanager(String queuemanager) {
		this.queuemanager = queuemanager;
		return this;
	}

	public final String getQUEUE() {
		return this.queue;
	}

	public IIBMQQ setQUEUE(final String queue) {
		this.queue = queue;
		return this;
	}

	public final int getCURDEPTH() {
		return this.curdepth;
	}

	public IIBMQQ setCURDEPTH(final int curdepth) {
		this.curdepth = curdepth;
		return this;
	}

	public final int getMAXDEPTH() {
		return this.maxdepth;
	}

	public IIBMQQ setMAXDEPTH(final int maxdepth) {
		this.maxdepth = maxdepth;
		return this;
	}

	public final int getIPPROCS() {
		return this.ipprocs;
	}

	public IIBMQQ setIPPROCS(final int ipprocs) {
		this.ipprocs = ipprocs;
		return this;
	}

	@Override
	public String toString() {
		return "IIBMQQ [hostname=" + hostname + ", queuemanager=" + queuemanager + ", queue=" + queue + ", curdepth="
				+ curdepth + ", maxdepth=" + maxdepth + ", ipprocs=" + ipprocs + "]";
	}

}
