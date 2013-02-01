package sandbox;

import java.io.IOException;
import java.io.InputStream;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MyVaadinApplication extends Application {

	private CssLayout sectionsMenuLayout = new CssLayout();
	private CssLayout userMenuLayout = new CssLayout();

	@Override
	public void init() {

		setTheme("grass");

		Window main = new Window();
		setMainWindow(main);

		// InputStream layoutFile = getClass().getResourceAsStream(
		// "/VAADIN/themes/grass/layouts/base.html");
		// CustomLayout layout = null;
		// try {
		// layout = new CustomLayout(layoutFile);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		CustomLayout layout = new CustomLayout("base");
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
		createSectionsMenu(layout);
		createUserMenu(layout);

		// obsah
		createContent(layout);

		// footer
		layout.addComponent(new Label("GRASS3"), "about");
	}

	private void createContent(CustomLayout layout) {
//		InputStream layoutFile = getClass().getResourceAsStream(
//				"/VAADIN/themes/grass/layouts/oneColumn.html");
//		CustomLayout contentLayout = null;
//		try {
//			contentLayout = new CustomLayout(layoutFile);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		CustomLayout contentLayout = new CustomLayout("oneColumn");
		
		layout.addComponent(contentLayout, "content");

		contentLayout.addComponent(new Label("Test"), "content");
	}

	private void createSectionsMenu(CustomLayout layout) {
		layout.addComponent(sectionsMenuLayout, "sectionsmenu");

		String[] strings = { "Domů", "Sekce", "Delší název sekce", "Něco",
				"Něco dalšího", "Poslední" };
		for (int i = 0; i < strings.length; i++) {
			Label item = new Label(strings[i]);
			item.addStyleName("item");
			item.setSizeUndefined();
			sectionsMenuLayout.addComponent(item);
		}
	}

	private void createUserMenu(CustomLayout layout) {
		layout.addComponent(userMenuLayout, "usermenu");

		String[] strings = { "Uživatel", "Nastavení", "Odhlásit" };
		for (int i = 0; i < strings.length; i++) {
			Label item = new Label(strings[i]);
			item.addStyleName("item");
			item.setSizeUndefined();
			userMenuLayout.addComponent(item);
		}
	}
}
