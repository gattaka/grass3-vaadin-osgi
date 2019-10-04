package cz.gattserver.grass3.monitor.web.label;

import com.vaadin.flow.component.Component;

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

	public WarningMonitorDisplay(String value, Component interComp) {
		super(new WarningMonitorStateLabel(), interComp, value);
	}

	public WarningMonitorDisplay(String value) {
		this(value, null);
	}

	public WarningMonitorDisplay() {
		this(null, null);
	}

}
