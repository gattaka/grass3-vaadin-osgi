package cz.gattserver.grass3.monitor.processor.item;

public class JVMThreadsMonitorItemTO extends MonitorItemTO {

	private long count;
	private long peak;

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getPeak() {
		return peak;
	}

	public void setPeak(long peak) {
		this.peak = peak;
	}

}
