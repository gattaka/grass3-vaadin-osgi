package cz.gattserver.grass3.recipes.ui;

import javax.servlet.http.HttpServletRequest;

import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.wexp.DispatchAction;
import cz.gattserver.grass3.wexp.in.impl.Label;
import cz.gattserver.grass3.wexp.in.impl.Link;
import cz.gattserver.grass3.wexp.in.impl.UI;

public class ListUI extends AbstractUI {

	private static final long serialVersionUID = 334621516108779566L;

	public ListUI(UI mainUI, UI prevUI) {

		Label nameLabel = new Label("přehled receptů");
		nameLabel.setCSSClass("recepty-centered-header");
		layout.addChild(nameLabel);

		for (RecipeDTO r : facade.getRecipes()) {
			Link menuItem;
			layout.addChild(menuItem = new Link(r.getName().toLowerCase(), new DispatchAction() {
				private static final long serialVersionUID = 5853456653676352799L;

				@Override
				public UI dispatch(HttpServletRequest req) {
					return new DetailUI(mainUI, ListUI.this, r.getId());
				}
			}));
			menuItem.setCSSClass("menu-item");
		}

		Link backLink;
		layout.addChild(backLink = new Link("zpět", new DispatchAction() {
			private static final long serialVersionUID = -2550135641464964288L;

			@Override
			public UI dispatch(HttpServletRequest req) {
				return mainUI;
			}
		}));
		backLink.setCSSClass("back-item");

	}

}
