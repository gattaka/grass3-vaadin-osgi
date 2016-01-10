package cz.gattserver.grass3.recipes.ui;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.SpringContextHelper;
import cz.gattserver.grass3.recipes.facades.IRecipeFacade;
import cz.gattserver.grass3.wexp.in.impl.UI;
import cz.gattserver.grass3.wexp.in.impl.VerticalLayout;
import cz.gattserver.grass3.wexp.servlet.WexpServlet;

public class AbstractUI extends UI {

	private static final long serialVersionUID = 4763865418322609840L;

	@Autowired
	protected transient IRecipeFacade facade;

	protected VerticalLayout layout = new VerticalLayout();

	public AbstractUI() {

		SpringContextHelper.inject(this);

		// html CSS
		HttpServletRequest req = WexpServlet.getCurrentHttpServletRequest();
		String uri = req.getRequestURI().toString();
		String path = req.getPathInfo();
		String prefix = path == null ? uri : uri.substring(0, uri.length() - path.length());
		setCSSFile(prefix + WexpServlet.WEXP_RESOURCE_PATH + "/css/recepty-styles.css");

		setContent(layout);

	}
}
