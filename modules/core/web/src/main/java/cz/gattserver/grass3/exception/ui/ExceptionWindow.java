package cz.gattserver.grass3.exception.ui;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.web.common.window.MessageWindow;

public abstract class ExceptionWindow extends MessageWindow {

	private static final long serialVersionUID = -2077736292967107272L;

	VerticalLayout innerLayout;

	public ExceptionWindow() {
		super("Chyba", "Neočekávaná systémová chyba", new ThemeResource("img/tags/delete_16.png"));
	}

	protected abstract String getStackTrace();

	@Override
	protected void createDetails() {
		addStyleName("error-layout");

		final TextArea stackTraceText = new TextArea();
		stackTraceText.setValue(getStackTrace());
		stackTraceText.setEnabled(false);
		stackTraceText.addStyleName("error-text-field");

		stackTraceText.setVisible(true);
		stackTraceText.setWidth("100%");
		stackTraceText.setHeight("500px");
		addComponent(stackTraceText);
		setWidth("1200px");
	}

}
