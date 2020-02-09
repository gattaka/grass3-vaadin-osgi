package cz.gattserver.grass3.ui.pages.template;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;

import cz.gattserver.grass3.exception.GrassPageException;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@Tag(Tag.DIV)
public class ErrorPage extends OneColumnPage implements HasErrorParameter<GrassPageException> {

	private static final long serialVersionUID = 4576353466500365046L;

	private GrassPageException exception;

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<GrassPageException> parameter) {
		this.exception = parameter.getException();
		init();
		return parameter.getException().getStatus();
	}

	@Override
	protected void createColumnContent(Div layout) {
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSpacing(true);
		horizontalLayout.setPadding(true);
		horizontalLayout.setWidthFull();

		Div div = new Div();
		div.setText(getErrorText(exception.getStatus()));
		div.addClassName("error-label");
		Image img = new Image(getErrorImage(exception.getStatus()), "Chyba");

		horizontalLayout.add(img);
		horizontalLayout.add(div);
		horizontalLayout.setFlexGrow(1, div);
		horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

		layout.add(horizontalLayout);
	}

	protected String getErrorText(int status) {
		switch (status) {
		case 403:
			return "403 - Nemáte oprávnění k provedení této operace";
		case 404:
			return "404 - Hledaný obsah neexistuje";
		case 500:
		default:
			return "500 - Došlo k chybě na straně serveru";
		}
	};

	protected String getErrorImage(int status) {
		switch (status) {
		case 403:
			return "img/403.png";
		case 404:
			return "img/404.png";
		case 500:
		default:
			return "img/500.png";
		}
	}

}
