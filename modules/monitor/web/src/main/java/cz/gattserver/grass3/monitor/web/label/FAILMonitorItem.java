package cz.gattserver.grass3.monitor.web.label;

public class FAILMonitorItem extends MonitorItem {

	private static final long serialVersionUID = 6591217631216026039L;
	private final static String PREFIX = "[ FAIL ]";
	private final static String LOG_STYLE_CLASS = "system-monitor-fail-log-style";

	public FAILMonitorItem(String value) {
		super(PREFIX, LOG_STYLE_CLASS, value);
	}

}
