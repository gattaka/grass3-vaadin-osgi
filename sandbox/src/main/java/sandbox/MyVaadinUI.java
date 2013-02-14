package sandbox;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

/**
 * The Application's "main" class
 */
public class MyVaadinUI extends UI {

	private static final long serialVersionUID = -7296936167498820319L;

	Navigator navigator;

	enum Views {
		EDITOR, VIEW
	};

	@Override
	protected void init(VaadinRequest request) {

		String path = request.getPathInfo();
		System.out.println("Path: " + path);

		// Create the navigator to control the page
		navigator = new Navigator(this, this);

		// Create and register the views
		navigator.addView(Views.EDITOR.name(), new EditorPage(navigator));
		navigator.addView(Views.VIEW.name(), new ViewPage(navigator));

		// Navigate to the start view
		navigator.navigateTo(Views.VIEW.name());

	}
}
