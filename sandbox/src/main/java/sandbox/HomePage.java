package sandbox;

import java.io.IOException;

import sandbox.ViewPage.ViewPageFactory;
import sandbox.interfaces.IPageFactory;
import sandbox.util.GrassRequest;

import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.JavaScript;
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

		JQueryAccordion accordion;
		try {
			accordion = new JQueryAccordion("Tlačítka 1", "Tlačítka 2",
					"Tlačítka 3");
			verticalLayout.addComponent(accordion);

			CssLayout buttonsLayout0 = new CssLayout();
			buttonsLayout0.addComponent(new Button("Btn1"));
			buttonsLayout0.addComponent(new Button("Tlačítko 2"));
			buttonsLayout0.addComponent(new Button("Něco 3"));
			buttonsLayout0.addComponent(new Button("Btn 4"));
			accordion.setElement(buttonsLayout0, 0);

			CssLayout buttonsLayout1 = new CssLayout();
			buttonsLayout1.addComponent(new Button("Btn1"));
			buttonsLayout1.addComponent(new Button("Tlačítko 2"));
			buttonsLayout1.addComponent(new Button("Tlačítko 2"));
			buttonsLayout1.addComponent(new Button("Něco 3"));
			buttonsLayout1.addComponent(new Button("Tlačítko 2"));
			buttonsLayout1.addComponent(new Button("Btn 4"));
			accordion.setElement(buttonsLayout1, 1);

			CssLayout buttonsLayout2 = new CssLayout();
			buttonsLayout2.addComponent(new Button("Btn1"));
			buttonsLayout2.addComponent(new Button("Btn 4"));
			buttonsLayout2.addComponent(new Button("Btn11"));
			buttonsLayout2.addComponent(new Button("Btn15dX"));
			accordion.setElement(buttonsLayout2, 2);

		} catch (IOException e) {
			e.printStackTrace();
		}

		addComponent(new JQueryComponent());

		JavaScript
				.eval("$( \"#accordion\" ).accordion({ event: \"mouseover\" });");

		// addComponent(new Label(
		// "<script>$( \"#accordion\" ).accordion({ event: \"mouseover\" });</script>",
		// ContentMode.HTML));

		contentLayout.addComponent(verticalLayout, "content");
	}

}
