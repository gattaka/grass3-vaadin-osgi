package cz.gattserver.grass3.monitor.processor.item;

import java.time.LocalDateTime;

public class LastBackupTimeMonitorItemTO extends MonitorItemTO {

	private String value;
	private LocalDateTime lastTime;

	public LocalDateTime getLastTime() {
		return lastTime;
	}

	public void setLastTime(LocalDateTime lastTime) {
		this.lastTime = lastTime;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
