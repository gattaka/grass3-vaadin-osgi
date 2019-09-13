package cz.gattserver.grass3.ui.pages;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass3.ui.pages.template.OneColumnPage;

@Route(value = "noservice")
public class NoServicePage extends OneColumnPage {

	private static final long serialVersionUID = -3691575066388167709L;

	public NoServicePage() {
		init();
	}

	@Override
	protected void createColumnContent(Div layout) {
		layout.add(new Span("Chybí služba pro čtení tohoto typu obsahu"));
	}

}
