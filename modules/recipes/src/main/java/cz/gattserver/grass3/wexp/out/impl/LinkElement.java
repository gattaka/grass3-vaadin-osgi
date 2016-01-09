package cz.gattserver.grass3.wexp.out.impl;

import cz.gattserver.grass3.wexp.out.TagElement;

public class LinkElement extends TagElement {

	private static final String ADDRESS_ATTRIBUTE = "href";
	private static final String TARGET_ATTRIBUTE = "target";

	public LinkElement(String address) {
		setAddress(address);
	}

	public LinkElement setAddress(String address) {
		setAttribute(ADDRESS_ATTRIBUTE, address);
		return this;
	}

	public String getAddress() {
		return getAttribute(ADDRESS_ATTRIBUTE);
	}

	public LinkElement setTarget(String target) {
		setAttribute(TARGET_ATTRIBUTE, target);
		return this;
	}

	public String getTarget() {
		return getAttribute(TARGET_ATTRIBUTE);
	}

	@Override
	protected String getTagName() {
		return "a";
	}

}
