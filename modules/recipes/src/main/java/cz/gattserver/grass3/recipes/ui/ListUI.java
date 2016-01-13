package cz.gattserver.grass3.recipes.ui;

import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.wexp.Request;
import cz.gattserver.grass3.wexp.in.impl.Label;
import cz.gattserver.grass3.wexp.in.impl.Link;

public class ListUI extends AbstractUI {

	private static final long serialVersionUID = 334621516108779566L;

	public static final String PATH = "list";

	public ListUI() {

		Label nameLabel = new Label("přehled receptů");
		nameLabel.setCSSClass("recepty-centered-header");
		layout.addChild(nameLabel);

		for (RecipeDTO r : facade.getRecipes()) {
			Link menuItem;
			layout.addChild(menuItem = new Link(r.getName().toLowerCase(), Request.createPath(DetailUI.PATH, r.getId()
					.toString())));
			menuItem.setCSSClass("menu-item");
		}

		Link backLink;
		layout.addChild(backLink = new Link("zpět", Request.createPath()));
		backLink.setCSSClass("back-item");

	}

}
