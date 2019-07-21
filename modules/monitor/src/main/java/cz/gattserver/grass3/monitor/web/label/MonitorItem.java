package cz.gattserver.grass3.monitor.web.label;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class MonitorItem extends HorizontalLayout {

	private static final long serialVersionUID = -4731895488246327123L;
	public static final String LOG_STYLE_CLASS = "system-monitor-log-style";

	public MonitorItem(MonitorStateLabel stateLabel, Component interComp, String value) {
		addComponent(stateLabel);

		if (interComp != null)
			addComponent(interComp);

		if (value != null) {
			Label outputLabel = new MonitorOutputLabel(value);
			addComponent(outputLabel);
		}
	}

}
