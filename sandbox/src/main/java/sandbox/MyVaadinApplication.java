package sandbox;

import java.io.IOException;
import java.io.InputStream;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MyVaadinApplication extends Application {

	private HorizontalLayout sectionsMenuLayout = new HorizontalLayout();
	private HorizontalLayout userMenuLayout = new HorizontalLayout();
	
	@Override
	public void init() {

		setTheme("grass");

		Window main = new Window();
		setMainWindow(main);

		InputStream layoutFile = getClass().getResourceAsStream(
				"/VAADIN/themes/grass/layouts/base.html");
		CustomLayout layout = null;
		try {
			layout = new CustomLayout(layoutFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// CustomLayout layout = new CustomLayout("base");
		main.setContent(layout);

		// homelink
		Link homelink = new Link();
		homelink.addStyleName("homelink");
		homelink.setResource(new ExternalResource(main.getURL()));
		homelink.setIcon(new ThemeResource("img/logo.png"));
		layout.addComponent(homelink, "homelink");

		// hlášky
		Label quotes = new Label("Quote");
		quotes.addStyleName("quotes");
		layout.addComponent(quotes, "quote");

		// menu
		HorizontalLayout menuLayout = new HorizontalLayout();
		layout.addComponent(menuLayout, "menu");
		createMenu(menuLayout);

		// obsah
		layout.addComponent(new Label("Test"), "content");
	}
	
	private void createMenu(HorizontalLayout layout) {

		layout.setStyleName("menu");
		layout.setWidth("990px");
		layout.setHeight("41px");
		layout.setMargin(false, true, false, true);

		// sekce menu
		createSectionsMenu(layout);

		// user menu
		createUserMenu(layout);
	}
	
	private void createSectionsMenu(HorizontalLayout layout) {
		layout.addComponent(sectionsMenuLayout);
		layout.setComponentAlignment(sectionsMenuLayout, Alignment.MIDDLE_LEFT);
		sectionsMenuLayout.setStyleName("sections");

		sectionsMenuLayout.addComponent(new Label("item #1"));
		sectionsMenuLayout.addComponent(new Label("item #2"));
		sectionsMenuLayout.addComponent(new Label("item #3"));
	}
	
	private void createUserMenu(HorizontalLayout layout) {
		layout.addComponent(userMenuLayout);
		layout.setComponentAlignment(userMenuLayout, Alignment.MIDDLE_RIGHT);
		userMenuLayout.setStyleName("usermenu");
		
		userMenuLayout.addComponent(new Label("useritem #1"));
		userMenuLayout.addComponent(new Label("useritem #2"));
	}
}
