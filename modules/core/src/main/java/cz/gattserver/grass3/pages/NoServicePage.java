package cz.gattserver.grass3.pages;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

public class NoServicePage extends OneColumnPage {

	public NoServicePage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		VerticalLayout layout = new VerticalLayout();

		Label label = new Label("Chybí služba pro čtení tohoto typu obsahu");
		layout.addComponent(label);
		layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

		layout.setMargin(true);
		layout.setSizeFull();

		return layout;
	}

}
