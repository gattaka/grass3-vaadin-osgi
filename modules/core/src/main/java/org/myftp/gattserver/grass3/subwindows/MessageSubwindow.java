package org.myftp.gattserver.grass3.subwindows;

import com.vaadin.terminal.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

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

		VerticalLayout subWindowlayout = new VerticalLayout();
		setContent(subWindowlayout);
		subWindowlayout.setMargin(true);
		subWindowlayout.setSpacing(true);
		subWindowlayout.setSizeFull();

		HorizontalLayout messageLayout = new HorizontalLayout();
		subWindowlayout.addComponent(messageLayout);

		messageLayout.addComponent(new Embedded(null, imageResource));
		messageLayout.addComponent(new Label(labelCaption));

		Button proceedButton = new Button("OK", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				onProceed(event);
				getParent().removeWindow(MessageSubwindow.this);
			}
		});

		subWindowlayout.addComponent(proceedButton);
		subWindowlayout.setComponentAlignment(proceedButton,
				Alignment.BOTTOM_RIGHT);

		// Zaměř se na nové okno
		focus();

	}

	protected void onProceed(ClickEvent event) {
	}

}
