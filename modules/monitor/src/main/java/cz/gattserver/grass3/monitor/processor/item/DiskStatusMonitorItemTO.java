package cz.gattserver.grass3.monitor.processor.item;

public class DiskStatusMonitorItemTO extends MonitorItemTO {

	private String name;
	private String type;
	private long total;
	private long usable;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getUsable() {
		return usable;
	}

	public void setUsable(long usable) {
		this.usable = usable;
	}

	public long getUsed() {
		return total - usable;
	}

	public float getUsedRation() {
		return (float) getUsed() / total;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
