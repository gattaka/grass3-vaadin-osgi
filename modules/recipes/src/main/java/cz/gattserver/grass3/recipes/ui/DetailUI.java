package cz.gattserver.grass3.recipes.ui;

import javax.servlet.http.HttpServletRequest;

import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.wexp.DispatchAction;
import cz.gattserver.grass3.wexp.in.impl.HorizontalLayout;
import cz.gattserver.grass3.wexp.in.impl.Label;
import cz.gattserver.grass3.wexp.in.impl.Link;
import cz.gattserver.grass3.wexp.in.impl.UI;
import cz.gattserver.grass3.wexp.in.impl.VerticalLayout;

public class DetailUI extends AbstractUI {

	private static final long serialVersionUID = -5896855010267149591L;

	public DetailUI(UI mainUI, UI prevUI, Long id) {

		VerticalLayout layout = new VerticalLayout();
		setContent(layout);

		RecipeDTO r = facade.getRecipeById(id);

		Label nameLabel = new Label(r.getName().toLowerCase());
		nameLabel.setCSSClass("recepty-centered-header");
		layout.addChild(nameLabel);

		Label descLabel = new Label(r.getDescription());
		layout.addChild(descLabel);

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		layout.addChild(horizontalLayout);

		Link backLink;
		horizontalLayout.addChild(backLink = new Link("zpět", new DispatchAction() {
			private static final long serialVersionUID = -2550135641464964288L;

			@Override
			public UI dispatch(HttpServletRequest req) {
				return prevUI;
			}
		}));
		backLink.setCSSClass("back-item");

		Link editLink;
		horizontalLayout.addChild(editLink = new Link("upravit", new DispatchAction() {
			private static final long serialVersionUID = -2550135641464964288L;

			@Override
			public UI dispatch(HttpServletRequest req) {
				return new CreateUI(mainUI, DetailUI.this, r.getId());
			}
		}));
		editLink.setCSSClass("back-item");

	}

}
