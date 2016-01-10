package cz.gattserver.grass3.recipes.ui;

import cz.gattserver.grass3.wexp.DispatchAction;
import cz.gattserver.grass3.wexp.in.impl.Label;
import cz.gattserver.grass3.wexp.in.impl.Link;
import cz.gattserver.grass3.wexp.in.impl.UI;

public class CreateUI extends AbstractUI {

	private static final long serialVersionUID = 3785137433928397710L;

	public CreateUI(UI mainUI) {

		Label nameLabel = new Label("přidat recept");
		nameLabel.setCSSClass("recepty-centered-header");
		layout.addChild(nameLabel);

		// TODO

		Link backLink;
		layout.addChild(backLink = new Link("zpět", new DispatchAction() {
			private static final long serialVersionUID = -2550135641464964288L;

			@Override
			public UI dispatch() {
				return mainUI;
			}
		}));
		backLink.setCSSClass("menu-item");
		backLink.setCSSClass("back-item");
	}
}
