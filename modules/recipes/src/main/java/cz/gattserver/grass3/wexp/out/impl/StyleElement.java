package cz.gattserver.grass3.wexp.out.impl;

import cz.gattserver.grass3.wexp.out.TagElement;

public class StyleElement extends TagElement {

	public StyleElement() {
		setAttribute("type", "text/css");
	}

	@Override
	protected String getTagName() {
		return "style";
	}

}
