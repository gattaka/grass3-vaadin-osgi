package sandbox;

import java.util.LinkedHashSet;
import java.util.Set;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public abstract class BaseWindow extends BackgroundWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	private HorizontalLayout sectionsMenuLayout = new HorizontalLayout();
	private HorizontalLayout userMenuLayout = new HorizontalLayout();
	private Label quotes;

	private Set<String> initJS = new LinkedHashSet<String>();

	/**
	 * Přihlásí skripty
	 * 
	 * @param initJS
	 */
	protected void submitInitJS(Set<String> initJS) {
		initJS.add("/VAADIN/themes/grass/js/grass.js");
	}

	/**
	 * Vezme všechen nahlášený JS init obsah a provede ho (kaskádově s ohledem
	 * na závislosti)
	 */
	private void gatherInitJS() {

		StringBuilder loadScript = new StringBuilder();

		// nejprve jQuery
		loadScript
				.append("var head= document.getElementsByTagName('head')[0];")
				.append("var script= document.createElement('script');")
				.append("script.type= 'text/javascript';")
				.append("script.src= '/VAADIN/themes/grass/js/jquery.js';")
				.append("var callback = function() {");

		// ostatní JS už lze nahrávat pomocí jQuery
		for (String js : initJS) {
			loadScript.append("$.getScript('" + js + "', function(){");
		}
		// uzavřít
		for (int i = 0; i < initJS.size(); i++) {
			loadScript.append("});");
		}

		// konec jQuery
		loadScript.append("};").append("script.onreadystatechange = callback;")
				.append("script.onload = callback;")
				.append("head.appendChild(script);");

		// fire !
		executeJavaScript(loadScript.toString());

	}

	@Override
	protected void onShow() {

		submitInitJS(initJS);
		gatherInitJS();

		// update menu sekcí
		populateSectionsMenu();

		// update menu uživatele
		populateUserMenu();

		// update hlášek
		quotes.setCaption(chooseQuote());

	}

	private void populateSectionsMenu() {
		sectionsMenuLayout.removeAllComponents();

		// link na domovskou stránku
		createHomeLink();
	}

	private void populateUserMenu() {
		userMenuLayout.removeAllComponents();

		// Přihlášení
		userMenuLayout.addComponent(new Label("Přihlášení"));

	}

	private String chooseQuote() {
		return "quote";
	}

	protected void buildHeader(HorizontalLayout headerLayout) {

		// Hlášky - generují se znova a znova
		quotes = new Label(chooseQuote());
		quotes.setStyleName("quotes");
		quotes.setWidth("740px");
		headerLayout.addComponent(quotes);
	}

	protected void buildBody(VerticalLayout layout) {

		VerticalLayout bodyLayout = new VerticalLayout();
		layout.addComponent(bodyLayout);
		layout.setComponentAlignment(bodyLayout, Alignment.TOP_CENTER);

		bodyLayout.setStyleName("body_layout");
		bodyLayout.setWidth("990px");
		bodyLayout.setMargin(false, false, true, false);

		// menu stránky
		createMenu(bodyLayout);

		// obsah stránky
		createWindowContent(bodyLayout);

	}

	private void createMenu(VerticalLayout layout) {

		// menu (centrovací element)
		HorizontalLayout menuHolderLayout = new HorizontalLayout();

		layout.addComponent(menuHolderLayout);
		menuHolderLayout.setStyleName("menu_holder");
		menuHolderLayout.setWidth("990px");
		menuHolderLayout.setHeight("41px");
		menuHolderLayout.setMargin(false, true, false, true);

		// sekce menu
		createSectionsMenu(menuHolderLayout);

		// user menu
		createUserMenu(menuHolderLayout);
	}

	private void createHomeLink() {
		Label link = new Label("Domů");
		link.setStyleName("first_menu_item");
		sectionsMenuLayout.addComponent(link);
	}

	private void createSectionsMenu(HorizontalLayout layout) {
		layout.addComponent(sectionsMenuLayout);
		layout.setComponentAlignment(sectionsMenuLayout, Alignment.MIDDLE_LEFT);
		sectionsMenuLayout.setStyleName("sections_menu_layout");

		// Domů
		createHomeLink();
	}

	private void createUserMenu(HorizontalLayout layout) {
		layout.addComponent(userMenuLayout);
		layout.setComponentAlignment(userMenuLayout, Alignment.MIDDLE_RIGHT);
		userMenuLayout.setStyleName("user_menu_layout");
	}

	/**
	 * Vytvoří obsah okna - tedy všechno to, co je mezi menu a footerem
	 * 
	 * @param layout
	 *            layout, do kterého se má vytvářet
	 */
	protected abstract void createWindowContent(VerticalLayout layout);

}
