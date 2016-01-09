package cz.gattserver.grass3.recipes.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.SpringContextHelper;
import cz.gattserver.grass3.recipes.facades.IRecipeFacade;
import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.recipes.web.in.impl.CSSRule;
import cz.gattserver.grass3.recipes.web.in.impl.HorizontalLayout;
import cz.gattserver.grass3.recipes.web.in.impl.Label;
import cz.gattserver.grass3.recipes.web.in.impl.UI;
import cz.gattserver.grass3.recipes.web.in.impl.VerticalLayout;

public class RecipesServlet extends HttpServlet {
	private static final long serialVersionUID = 7172666112080085629L;

	@Autowired
	private IRecipeFacade facade;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if (facade == null) {
			SpringContextHelper.inject(this);
		}

		final OutputStream out = resp.getOutputStream();

		UI ui = new UI();

		// body CSS
		CSSRule bodyCSS = new CSSRule("body");
		bodyCSS.setStyle("font-family", "sans-serif");
		bodyCSS.setStyle("font-size", "25px");
		ui.setClass(bodyCSS);

		// recepty-header CSS
		CSSRule receptyHeaderCSS = new CSSRule(".recepty-header.grass-label");
		receptyHeaderCSS.setStyle("font-size", "35px");
		receptyHeaderCSS.setStyle("margin-bottom", "15px");
		ui.setClass(receptyHeaderCSS);

		VerticalLayout layout = new VerticalLayout();
		ui.setContent(layout);
		Label receptyLabel = new Label("Recepty");
		receptyLabel.setClass("recepty-header");
		layout.addChild(receptyLabel);

		for (RecipeDTO r : facade.getRecipes()) {
			layout.addChild(new Label(r.getDescription()));
		}

		HorizontalLayout hor = new HorizontalLayout();
		hor.addChild(new Label("one"));
		hor.addChild(new Label("two"));
		layout.addChild(hor);

		layout.setWidth("800px");

		ui.construct().write(out);
		out.flush();
		out.close();
	}
}
