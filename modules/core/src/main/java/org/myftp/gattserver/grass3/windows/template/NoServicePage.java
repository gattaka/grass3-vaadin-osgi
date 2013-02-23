package org.myftp.gattserver.grass3.windows.template;

import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.windows.ifces.PageFactory;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class NoServicePage extends BasePage {

	private static final long serialVersionUID = 8907394744054280981L;

	public static enum NoServicePageFactory implements PageFactory {

		INSTANCE;

		@Override
		public String getPageName() {
			return "noservice";
		}

		@Override
		public Component createPage(GrassRequest request) {
			return new NoServicePage(request);
		}
	}

	public NoServicePage(GrassRequest request) {
		super(request);
	}


	@Override
	protected void createContent(CustomLayout layout) {
		CustomLayout contentLayout = new CustomLayout("oneColumn");
		layout.addComponent(contentLayout, "content");

		VerticalLayout pagelayout = new VerticalLayout();
		Label label = new Label("Chybí služba pro čtení tohoto typu obsahu");
		pagelayout.addComponent(label);
		pagelayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

		pagelayout.setMargin(true);
		pagelayout.setSizeFull();

		contentLayout.addComponent(pagelayout, "content");
	}

}
