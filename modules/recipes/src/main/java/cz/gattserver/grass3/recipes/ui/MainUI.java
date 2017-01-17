package cz.gattserver.grass3.recipes.ui;

import cz.gattserver.grass3.wexp.Request;
import cz.gattserver.grass3.wexp.in.impl.Label;
import cz.gattserver.grass3.wexp.in.impl.Link;

public class MainUI extends AbstractUI {

	private static final long serialVersionUID = 3291998307032442398L;

	public MainUI() {

		Label nameLabel = new Label("recepty");
		nameLabel.setCSSClass("recepty-main-header");
		layout.addChild(nameLabel);

		Link listLink;
		layout.addChild(listLink = new Link("přehled receptů", Request.createPath(ListUI.PATH)));
		listLink.setCSSClass("menu-item");

		Link createLink;

		layout.addChild(createLink = new Link("založit recept", req -> {
			return new CreateUI(MainUI.this);
		}));

		createLink.setCSSClass("menu-item");

	}
}
