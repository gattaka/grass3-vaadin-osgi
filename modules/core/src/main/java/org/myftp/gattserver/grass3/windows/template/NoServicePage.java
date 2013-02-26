package org.myftp.gattserver.grass3.windows.template;

import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.windows.ifces.PageFactory;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class NoServicePage extends OneColumnPage {

	private static final long serialVersionUID = 8907394744054280981L;

	public static final PageFactory FACTORY = new PageFactory("noservice") {
		@Override
		public GrassPage createPage(GrassRequest request) {
			return new NoServicePage(request);
		}
	};

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
