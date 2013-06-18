package sandbox;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/**
 * The Application's "main" class
 */
@Title("Gattserver")
@Theme("grass")
@Scope("prototype")
@Component("grassUI")
public class MyVaadinUI extends UI {

	private static final long serialVersionUID = -7296936167498820319L;

	@Override
	protected void init(VaadinRequest request) {

		VerticalLayout layout = new VerticalLayout();


		setContent(layout);

	}
}
