package cz.gattserver.grass3.monitor.web.label;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class MonitorItem extends HorizontalLayout {

	private static final long serialVersionUID = -4731895488246327123L;
	private final static String LOG_STYLE_CLASS = "system-monitor-log-style";

	public MonitorItem(String prefix, String styleClass, String value) {
		Label prefixLabel = new Label("<span class=\"" + styleClass + "\">" + prefix + "</span>");
		prefixLabel.setContentMode(ContentMode.HTML);
		prefixLabel.setWidth("60px");
		addComponent(prefixLabel);

		Label outputLabel = new Label("<span class=\"" + LOG_STYLE_CLASS + "\">" + value + "</span>");
		outputLabel.setContentMode(ContentMode.HTML);
		outputLabel.setWidth("880px");
		addComponent(outputLabel);
	}

}
