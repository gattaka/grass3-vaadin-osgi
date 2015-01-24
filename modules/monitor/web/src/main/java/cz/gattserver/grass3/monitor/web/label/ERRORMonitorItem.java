package cz.gattserver.grass3.monitor.web.label;

public class ERRORMonitorItem extends MonitorItem {

	private static final long serialVersionUID = 6591217631216026039L;
	private final static String PREFIX = "[ ERR! ]";
	private final static String LOG_STYLE_CLASS = "system-monitor-error-log-style";

	public ERRORMonitorItem(String value) {
		super(PREFIX, LOG_STYLE_CLASS, value);
	}

}
