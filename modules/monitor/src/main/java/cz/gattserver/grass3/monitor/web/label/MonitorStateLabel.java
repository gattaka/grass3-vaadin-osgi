package cz.gattserver.grass3.monitor.web.label;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

public class MonitorStateLabel extends Label {

	private static final long serialVersionUID = 7228246273667002433L;

	public MonitorStateLabel(String style, String value) {
		super("<span class=\"" + style + "\">" + value + "</span>");
		setContentMode(ContentMode.HTML);
		setWidth(null);
	}
}
