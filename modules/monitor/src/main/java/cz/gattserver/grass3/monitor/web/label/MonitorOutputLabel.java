package cz.gattserver.grass3.monitor.web.label;

import cz.gattserver.web.common.ui.HtmlDiv;

public class MonitorOutputLabel extends HtmlDiv {

	private static final long serialVersionUID = 7228246273667002433L;

	public MonitorOutputLabel(String value) {
		super("<span class=\"" + MonitorItem.LOG_STYLE_CLASS + "\">" + value + "</span>");
		setWidth(null);
	}
}
