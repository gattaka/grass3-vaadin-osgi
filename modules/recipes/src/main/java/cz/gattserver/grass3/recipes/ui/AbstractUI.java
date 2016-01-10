package cz.gattserver.grass3.recipes.ui;

import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.SpringContextHelper;
import cz.gattserver.grass3.recipes.facades.IRecipeFacade;
import cz.gattserver.grass3.wexp.in.impl.UI;
import cz.gattserver.grass3.wexp.in.impl.VerticalLayout;
import cz.gattserver.grass3.wexp.servlet.WexpServlet;

public abstract class AbstractUI extends UI {

	private static final long serialVersionUID = 4763865418322609840L;

	@Autowired
	protected transient IRecipeFacade facade;

	protected VerticalLayout layout = new VerticalLayout();

	public AbstractUI() {

		SpringContextHelper.inject(this);

		// html CSS
		setCSSFile(WexpServlet.getPathPrefix() + WexpServlet.WEXP_RESOURCE_PATH + "/css/recepty-styles.css");

		setContent(layout);

	}
}
