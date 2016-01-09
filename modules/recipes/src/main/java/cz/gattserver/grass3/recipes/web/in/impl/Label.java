package cz.gattserver.grass3.recipes.web.in.impl;

import cz.gattserver.grass3.recipes.web.in.Component;
import cz.gattserver.grass3.recipes.web.out.WebElement;
import cz.gattserver.grass3.recipes.web.out.impl.DivElement;
import cz.gattserver.grass3.recipes.web.out.impl.TextElement;

public class Label extends Component {

	private String caption;

	public Label(String caption) {
		this.caption = caption;
	}

	@Override
	public WebElement construct() {
		DivElement div = new DivElement();
		div.setClass("grass-label");
		div.addChild(new TextElement(caption));
		return div;
	}

}
