package org.myftp.gattserver.grass3.windows;

import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.windows.template.BasePage;
import org.myftp.gattserver.grass3.windows.template.IPageFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class HomePage extends BasePage {

	private static final long serialVersionUID = 5355366043081283263L;

	public static enum HomePageFactory implements IPageFactory {

		INSTANCE;

		@Override
		public String getPageName() {
			return "home";
		}

		@Override
		public Component createPage(GrassRequest request) {
			return new HomePage(request);
		}
	}

	public HomePage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void createContent(CustomLayout layout) {

		CustomLayout contentLayout = new CustomLayout("oneColumn");
		layout.addComponent(contentLayout, "content");

		VerticalLayout verticalLayout = new VerticalLayout();

		final TextField field = new TextField();

		verticalLayout.addComponent(field);

		contentLayout.addComponent(verticalLayout, "content");

	}

}
