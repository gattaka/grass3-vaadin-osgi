package cz.gattserver.grass3.wexp.in.impl;

import java.util.HashMap;
import java.util.Map;

public class CSSRule {

	private String name;
	private Map<String, String> styles;

	public CSSRule(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Map<String, String> getStyles() {
		return styles;
	}

	public CSSRule setStyle(String name, String value) {
		if (styles == null)
			styles = new HashMap<String, String>();
		styles.put(name, value);
		return this;
	}
}
