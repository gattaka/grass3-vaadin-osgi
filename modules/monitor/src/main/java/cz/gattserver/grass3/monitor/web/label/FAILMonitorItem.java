package cz.gattserver.grass3.monitor.web.label;

import com.vaadin.ui.Component;

/**
 * Info o tom, že se monitorovací informaci nezdařilo
 * získat/přečíst/interpretovat. Neznamená, že stav monitorovaného předmětu je
 * nevyhovující -- stav pouze není možné určit.
 * 
 * @author Hynek
 *
 */
public class FAILMonitorItem extends MonitorItem {

	private static final long serialVersionUID = 6591217631216026039L;
	private final static String PREFIX = "[ FAIL ]";
	private final static String LOG_STYLE_CLASS = "system-monitor-fail-log-style";

	public FAILMonitorItem(String value, Component interComp) {
		super(PREFIX, LOG_STYLE_CLASS, interComp, value);
	}

	public FAILMonitorItem(String value) {
		this(value, null);
	}

}
