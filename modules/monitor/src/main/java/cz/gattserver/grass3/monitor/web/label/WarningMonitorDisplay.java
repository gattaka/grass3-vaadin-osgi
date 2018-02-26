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
public class WarningMonitorDisplay extends MonitorItem {

	private static final long serialVersionUID = 6591217631216026039L;
	private final static String PREFIX = "[ WARN ]";
	private final static String LOG_STYLE_CLASS = "system-monitor-warn-log-style";

	public WarningMonitorDisplay(String value, Component interComp) {
		super(PREFIX, LOG_STYLE_CLASS, interComp, value);
	}

	public WarningMonitorDisplay(String value) {
		this(value, null);
	}

}
