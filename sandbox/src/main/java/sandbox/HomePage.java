package sandbox;

import sandbox.ViewPage.ViewPageFactory;
import sandbox.interfaces.IPageFactory;
import sandbox.util.GrassRequest;

import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Button.ClickEvent;
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

		Button button = new Button("View: " + field.getValue(),
				new Button.ClickListener() {
					private static final long serialVersionUID = 7646166365866861567L;

					@Override
					public void buttonClick(ClickEvent event) {
						Page.getCurrent().setLocation(
								"/" + ViewPageFactory.INSTANCE.getPageName()
										+ "/" + field.getValue());
					}
				});

		verticalLayout.addComponent(button);
		verticalLayout.addComponent(field);

		contentLayout.addComponent(verticalLayout, "content");

	}

}
