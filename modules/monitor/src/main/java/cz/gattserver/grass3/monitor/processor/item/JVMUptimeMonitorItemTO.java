package cz.gattserver.grass3.monitor.processor.item;

public class JVMUptimeMonitorItemTO extends MonitorItemTO {

	private long elapsedDays;
	private long elapsedHours;
	private long elapsedMinutes;
	private long elapsedSeconds;

	public long getElapsedDays() {
		return elapsedDays;
	}

	public long getElapsedHours() {
		return elapsedHours;
	}

	public long getElapsedMinutes() {
		return elapsedMinutes;
	}

	public long getElapsedSeconds() {
		return elapsedSeconds;
	}

	public void setUptime(long uptime) {
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;
		elapsedDays = uptime / daysInMilli;
		uptime = uptime % daysInMilli;
		elapsedHours = uptime / hoursInMilli;
		uptime = uptime % hoursInMilli;
		elapsedMinutes = uptime / minutesInMilli;
		uptime = uptime % minutesInMilli;
		elapsedSeconds = uptime / secondsInMilli;
	}

}
