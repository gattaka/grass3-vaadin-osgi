package cz.gattserver.grass3.ui.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;

public class NoServicePage extends OneColumnPage {

	public NoServicePage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createColumnContent() {
		VerticalLayout layout = new VerticalLayout();

		Span span = new Span("Chybí služba pro čtení tohoto typu obsahu");
		layout.add(span);
		layout.setHorizontalComponentAlignment(Alignment.CENTER, span);

		layout.setPadding(true);
		layout.setSizeFull();

		return layout;
	}

}
