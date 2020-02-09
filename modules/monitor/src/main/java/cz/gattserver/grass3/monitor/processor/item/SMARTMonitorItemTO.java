package cz.gattserver.grass3.monitor.processor.item;

public class SMARTMonitorItemTO extends MonitorItemTO {

	private String time;
	private String message;

	public SMARTMonitorItemTO() {
	}

	public SMARTMonitorItemTO(String time, String message) {
		this.time = time;
		this.message = message;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
