package cz.gattserver.grass3.recipes.ui;


import javax.servlet.http.HttpServletRequest;

import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.wexp.DispatchAction;
import cz.gattserver.grass3.wexp.in.impl.Label;
import cz.gattserver.grass3.wexp.in.impl.Link;
import cz.gattserver.grass3.wexp.in.impl.UI;
import cz.gattserver.grass3.wexp.in.impl.VerticalLayout;

public class DetailUI extends AbstractUI {

	private static final long serialVersionUID = -5896855010267149591L;

	public DetailUI(RecipeDTO r, UI mainUI) {

		VerticalLayout layout = new VerticalLayout();
		setContent(layout);

		Label nameLabel = new Label(r.getName().toLowerCase());
		nameLabel.setCSSClass("recepty-centered-header");
		layout.addChild(nameLabel);

		Label descLabel = new Label(r.getDescription());
		layout.addChild(descLabel);

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
