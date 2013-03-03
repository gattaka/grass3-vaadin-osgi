package sandbox;

import sandbox.util.GrassRequest;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.BaseTheme;

public abstract class BasePage extends CustomLayout {

	private static final long serialVersionUID = 502625699429764791L;

	private CssLayout sectionsMenuLayout = new CssLayout();
	private CssLayout userMenuLayout = new CssLayout();

	private GrassRequest request;
	
	public BasePage(GrassRequest request) {
		super("base");
		
		this.request = request; 

		// homelink
		Link homelink = new Link();
		homelink.addStyleName("homelink");
		homelink.setResource(new ExternalResource("/"));
		homelink.setIcon(new ThemeResource("img/logo.png"));
		addComponent(homelink, "homelink");

		// hlášky
		Label quotes = new Label("Quote");
		quotes.addStyleName("quotes");
		addComponent(quotes, "quote");

		// menu
		createSectionsMenu(this);
		createUserMenu(this);

		// obsah
		createContent(this);

		// footer
		addComponent(new Label("GRASS3"), "about");
	}

	protected abstract void createContent(CustomLayout layout);

	protected GrassRequest getRequest() {
		return request;
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

		Button btn = new Button("Btn1");
		btn.setStyleName(BaseTheme.BUTTON_LINK);
		userMenuLayout.addComponent(btn);
		
		String[] strings = { "Uživatel", "Nastavení", "Odhlásit" };
		for (int i = 0; i < strings.length; i++) {
			Label item = new Label(strings[i]);
			item.addStyleName("item");
			item.setSizeUndefined();
			userMenuLayout.addComponent(item);
		}
		
		Button btn2 = new Button("Btn2");
		btn.setStyleName(BaseTheme.BUTTON_LINK);
		userMenuLayout.addComponent(btn2);
	}

}
