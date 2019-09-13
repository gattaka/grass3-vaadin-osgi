package cz.gattserver.grass3.ui.dialogs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;

import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class DetailDialog extends WebDialog {

	private static final long serialVersionUID = -4989848867002620787L;

	public DetailDialog(String name) {
		super(name);
	}

	protected Span addDetailLine(String caption, String content) {
		Span label;
		layout.add(new Span("<strong>" + caption + "</strong>"));
		label = new Span(content);
		layout.add(label);
		return label;
	}

	protected void addDetailLine(String caption, Component content) {
		layout.add(new Span("<strong>" + caption + "</strong>"));
		layout.add(content);
	}

}
