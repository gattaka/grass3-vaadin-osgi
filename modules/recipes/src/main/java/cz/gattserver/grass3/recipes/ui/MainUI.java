package cz.gattserver.grass3.recipes.ui;

import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.wexp.DispatchAction;
import cz.gattserver.grass3.wexp.in.impl.Link;
import cz.gattserver.grass3.wexp.in.impl.UI;

public class MainUI extends AbstractUI {

	public MainUI() {

		for (RecipeDTO r : facade.getRecipes()) {
			layout.addChild(new Link(r.getName(), new DispatchAction() {

				@Override
				public UI dispatch() {
					return new DetailUI(r, MainUI.this);
				}
			}));
		}

	}
}
