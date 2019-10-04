package cz.gattserver.grass3.monitor.web.label;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class MonitorItem extends HorizontalLayout {

	private static final long serialVersionUID = -4731895488246327123L;
	public static final String LOG_STYLE_CLASS = "system-monitor-log-style";

	public MonitorItem(MonitorStateLabel stateLabel, Component interComp, String value) {
		add(stateLabel);

		if (interComp != null)
			add(interComp);

		if (value != null) {
			Div outputLabel = new MonitorOutputLabel(value);
			add(outputLabel);
		}
	}

}
