package org.myftp.gattserver.grass3.subwindows;

import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public abstract class MessageWindow extends GrassWindow {

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
	public MessageWindow(String caption, String labelCaption, Resource imageResource) {
		super(caption);

		center();
		setWidth("420px");

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSpacing(true);
		addComponent(horizontalLayout);

		Embedded embedded = new Embedded(null, imageResource);
		// embedded.addStyleName("msgimg");
		horizontalLayout.addComponent(embedded);
		horizontalLayout.setComponentAlignment(embedded, Alignment.MIDDLE_CENTER);

		CssLayout messageLayout = new CssLayout();
		horizontalLayout.addComponent(messageLayout);
		horizontalLayout.setComponentAlignment(messageLayout, Alignment.MIDDLE_CENTER);
		messageLayout.setWidth("100%");

		Label msgLabel = new Label(labelCaption);
		msgLabel.setSizeUndefined();
		// msgLabel.addStyleName("msglabel");
		messageLayout.addComponent(msgLabel);

		createDetails();

		Button proceedButton = new Button("OK", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				onProceed(event);
				close();
			}
		});

		addComponent(proceedButton);
		setComponentAlignment(proceedButton, Alignment.BOTTOM_RIGHT);

		// Zaměř se na nové okno
		// Window must be attached to parent before calling bringToFront method.
		// focus(); // od V7 to nejde

	}

	protected void createDetails() {
	};

	protected void onProceed(ClickEvent event) {
	}

}
