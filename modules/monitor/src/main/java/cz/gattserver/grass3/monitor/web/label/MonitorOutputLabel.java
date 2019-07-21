package cz.gattserver.grass3.monitor.web.label;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

public class MonitorOutputLabel extends Label {

	private static final long serialVersionUID = 7228246273667002433L;

	public MonitorOutputLabel(String value) {
		super("<span class=\"" + MonitorItem.LOG_STYLE_CLASS + "\">" + value + "</span>");
		setContentMode(ContentMode.HTML);
		setWidth(null);
	}
}
