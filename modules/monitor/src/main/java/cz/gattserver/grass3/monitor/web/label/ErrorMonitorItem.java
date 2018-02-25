package cz.gattserver.grass3.monitor.web.label;

import com.vaadin.ui.Component;

/**
 * Info o stavu monitorovaného předmětu se podařilo získat a stav je
 * nevyhovující.
 * 
 * @author Hynek
 *
 */
public class ErrorMonitorItem extends MonitorItem {

	private static final long serialVersionUID = 6591217631216026039L;
	private final static String PREFIX = "[ ERR! ]";
	private final static String LOG_STYLE_CLASS = "system-monitor-error-log-style";

	public ErrorMonitorItem(String value, Component interComp) {
		super(PREFIX, LOG_STYLE_CLASS, interComp, value);
	}

	public ErrorMonitorItem(String value) {
		this(value, null);
	}

}
