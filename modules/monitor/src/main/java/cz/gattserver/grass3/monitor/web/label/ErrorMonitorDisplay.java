package cz.gattserver.grass3.monitor.web.label;

import com.vaadin.flow.component.Component;

/**
 * Info o stavu monitorovaného předmětu se podařilo získat a stav je
 * nevyhovující.
 * 
 * @author Hynek
 *
 */
public class ErrorMonitorDisplay extends MonitorItem {

	private static final long serialVersionUID = 6591217631216026039L;

	public ErrorMonitorDisplay(String value, Component interComp) {
		super(new ErrorMonitorStateLabel(), interComp, value);
	}

	public ErrorMonitorDisplay(String value) {
		this(value, null);
	}

	public ErrorMonitorDisplay() {
		this(null, null);
	}

}
