package cz.gattserver.grass3.monitor.web.label;

public class OKMonitorItem extends MonitorItem {

	private static final long serialVersionUID = 6591217631216026039L;
	private final static String PREFIX = "[  OK  ]";
	private final static String LOG_STYLE_CLASS = "system-monitor-ok-log-style";

	public OKMonitorItem(String value) {
		super(PREFIX, LOG_STYLE_CLASS, value);
	}

}
