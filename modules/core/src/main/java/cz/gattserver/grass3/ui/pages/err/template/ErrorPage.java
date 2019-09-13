package cz.gattserver.grass3.ui.pages.err.template;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import cz.gattserver.grass3.ui.pages.template.OneColumnPage;

public abstract class ErrorPage extends OneColumnPage {

	private static final long serialVersionUID = 4576353466500365046L;

	@Override
	protected void createColumnContent(Div layout) {
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSpacing(true);
		horizontalLayout.setPadding(true);
		horizontalLayout.setWidth("100%");

		Span span = new Span(getErrorText());
		span.addClassName("error-label");
		Image img = new Image(getErrorImage(), "Chyba");

		horizontalLayout.add(img);
		horizontalLayout.add(span);
		horizontalLayout.setVerticalComponentAlignment(Alignment.START, img);
		horizontalLayout.setVerticalComponentAlignment(Alignment.CENTER, span);

		layout.add(horizontalLayout);
	}

	protected abstract String getErrorText();

	protected abstract String getErrorImage();

}
