package org.myftp.gattserver.grass3.hw.web;

import org.myftp.gattserver.grass3.hw.facade.IHWFacade;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class OverviewTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	public OverviewTab(IHWFacade hwFacade) {

		setSpacing(true);
		setMargin(true);

		addComponent(new Label("<strong>Aktuálně je evidováno: </strong>",
				ContentMode.HTML));
		addComponent(new Label(hwFacade.getAllHWItems().size() + " hw položek"));
		addComponent(new Label(hwFacade.getAllHWTypes().size()
				+ " typů hw položek"));
		addComponent(new Label(hwFacade.getAllServiceNotes().size()
				+ " servisních záznamů"));

	}
}
