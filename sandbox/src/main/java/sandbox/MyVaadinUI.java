package sandbox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MyVaadinUI extends UI {

	private int count = 0;

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);

		Button button = new Button("Click Me");
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				layout.addComponent(new Label("Thank you for clicking" + count));
				count++;
			}
		});
		layout.addComponent(button);

		String user = (String) VaadinSession.getCurrent().getAttribute("USER");
		if (user == null) {
			final TextField textField = new TextField("Jméno");
			layout.addComponent(textField);
			layout.addComponent(new Button("přihlásit",
					new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							String value = textField.getValue();
							if (value == null || value.isEmpty()) {
								Notification.show("prázdné jméno");
							} else {
								VaadinSession.getCurrent().setAttribute("USER",
										value);
							}
						}

					}));
		} else {
			layout.addComponent(new Label("Uživatel je přihlášen jako: " + user));
		}
	}
}
