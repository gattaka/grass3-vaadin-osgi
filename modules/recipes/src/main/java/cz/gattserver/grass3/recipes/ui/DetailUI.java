package cz.gattserver.grass3.recipes.ui;

import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.wexp.DispatchAction;
import cz.gattserver.grass3.wexp.in.impl.Label;
import cz.gattserver.grass3.wexp.in.impl.Link;
import cz.gattserver.grass3.wexp.in.impl.UI;
import cz.gattserver.grass3.wexp.in.impl.VerticalLayout;

public class DetailUI extends AbstractUI {

	public DetailUI(RecipeDTO r, UI mainUI) {

		VerticalLayout layout = new VerticalLayout();
		setContent(layout);

		Label nameLabel = new Label(r.getName());
		nameLabel.setClass("recepty-header");
		layout.addChild(nameLabel);

		Label descLabel = new Label(r.getDescription());
		layout.addChild(descLabel);

		layout.addChild(new Link("Zpět", new DispatchAction() {

			@Override
			public UI dispatch() {
				return mainUI;
			}
		}));

	}

}
