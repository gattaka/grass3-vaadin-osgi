package cz.gattserver.grass3.monitor.web.label;

import cz.gattserver.web.common.ui.HtmlDiv;

public class MonitorStateLabel extends HtmlDiv {

	private static final long serialVersionUID = 7228246273667002433L;

	public MonitorStateLabel(String style, String value) {
		super("<span class=\"" + style + "\">" + value + "</span>");
		setWidth(null);
	}
}
