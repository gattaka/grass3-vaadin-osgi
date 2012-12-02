package org.myftp.gattserver.grass3.subwindows;

import com.vaadin.terminal.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

public abstract class MessageSubwindow extends GrassSubWindow {

	private static final long serialVersionUID = 4123506060675738841L;

	/**
	 * Default konstruktor
	 * 
	 * @param caption
	 *            popisek okna
	 * @param labelCaption
	 *            obsah zprávy v okně
	 * @param imageResource
	 *            resource ikony okna
	 */
	public MessageSubwindow(String caption, String labelCaption,
			Resource imageResource) {
		super(caption);

		center();
		setWidth("220px");

		GridLayout subWindowlayout = new GridLayout(2, 2);
		setContent(subWindowlayout);
		subWindowlayout.setMargin(true);
		subWindowlayout.setSpacing(true);
		subWindowlayout.setSizeFull();

		subWindowlayout.addComponent(new Embedded(null, imageResource), 0, 0);
		subWindowlayout.addComponent(new Label(labelCaption), 1, 0);

		Button proceedButton = new Button("Ano", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				onProceed(event);
				getParent().removeWindow(MessageSubwindow.this);
			}
		});

		subWindowlayout.addComponent(proceedButton, 1, 1);
		subWindowlayout.setComponentAlignment(proceedButton,
				Alignment.MIDDLE_CENTER);

		// Zaměř se na nové okno
		focus();

	}

	protected void onProceed(ClickEvent event) {
	}

}
