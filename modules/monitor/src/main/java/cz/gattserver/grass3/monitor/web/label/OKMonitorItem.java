package cz.gattserver.grass3.monitor.web.label;

import com.vaadin.ui.Component;

/**
 * Info, že získaná monitorovací informace byla získána v pořádku a údaj je
 * vyhovující
 * 
 * @author Hynek
 *
 */
public class OKMonitorItem extends MonitorItem {

	private static final long serialVersionUID = 6591217631216026039L;
	private final static String PREFIX = "[  OK  ]";
	private final static String LOG_STYLE_CLASS = "system-monitor-ok-log-style";

	public OKMonitorItem(String value, Component interComp) {
		super(PREFIX, LOG_STYLE_CLASS, interComp, value);
	}

	public OKMonitorItem(String value) {
		this(value, null);
	}

}
