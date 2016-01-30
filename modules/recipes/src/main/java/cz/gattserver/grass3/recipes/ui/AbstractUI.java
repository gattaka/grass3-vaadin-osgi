package cz.gattserver.grass3.recipes.ui;

import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.SpringContextHelper;
import cz.gattserver.grass3.recipes.facades.IRecipeFacade;
import cz.gattserver.grass3.wexp.Request;
import cz.gattserver.grass3.wexp.in.impl.UI;
import cz.gattserver.grass3.wexp.in.impl.VerticalLayout;

public abstract class AbstractUI extends UI {

	private static final long serialVersionUID = 4763865418322609840L;

	@Autowired
	protected transient IRecipeFacade facade;

	protected VerticalLayout layout = new VerticalLayout();

	public AbstractUI() {

		SpringContextHelper.inject(this);

		// html CSS
		setCSSFile(Request.createResourcePath("css", "recepty-styles.css"));
		
		// html favicon
		setFavicon(Request.createResourcePath("img", "favicon.ico"));

		setContent(layout);

	}
}
