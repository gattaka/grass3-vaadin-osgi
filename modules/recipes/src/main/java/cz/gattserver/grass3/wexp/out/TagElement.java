package cz.gattserver.grass3.wexp.out;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class TagElement extends WebElement {

	private static final String STYLE_ATTRIBUTE = "style";
	private static final String CLASS_ATTRIBUTE = "class";

	protected abstract String getTagName();

	protected Set<WebElement> children;

	protected Map<String, String> attributes;
	protected Map<String, String> styles;
	protected Set<String> classes;

	public TagElement addChild(WebElement... childList) {
		if (children == null)
			children = new LinkedHashSet<WebElement>();
		for (WebElement child : childList) {
			children.add(child);
			// TODO kontrola, zda už tam je?
		}
		return this;
	}

	public boolean hasChild(WebElement child) {
		if (children == null)
			return false;
		return children.contains(child);
	}

	public TagElement setStyle(String name, String value) {
		if (styles == null)
			styles = new HashMap<String, String>();
		styles.put(name, value);
		return this;
	}

	public String getStyle(String name, String value) {
		if (styles == null)
			return null;
		return styles.get(name);
	}

	public TagElement setClass(String name) {
		if (classes == null)
			classes = new HashSet<String>();
		classes.add(name);
		return this;
	}

	public boolean hasClass(String name) {
		if (classes == null)
			return false;
		return classes.contains(name);
	}

	public TagElement setAttribute(String name, String value) {
		if (attributes == null)
			attributes = new HashMap<String, String>();
		attributes.put(name, value);
		return this;
	}

	public String getAttribute(String name) {
		if (attributes == null)
			return null;
		return attributes.get(name);
	}

	public String getOpenTag() {
		String tag = "<" + getTagName();

		// připrav style atribut
		if (styles != null) {
			String style = "";
			for (String key : styles.keySet()) {
				if (style.length() > 0) {
					style += ",";
				}
				style += key + ":" + styles.get(key);
			}
			setAttribute(STYLE_ATTRIBUTE, style);
		}

		// připrav class atribut
		if (classes != null) {
			String clasList = "";
			for (String clas : classes) {
				if (clasList.length() > 0) {
					clasList += " ";
				}
				clasList += clas;
			}
			setAttribute(CLASS_ATTRIBUTE, clasList);
		}

		if (attributes != null)
			for (String key : attributes.keySet())
				tag += " " + key + "=\"" + attributes.get(key) + "\"";

		tag += ">";
		return tag;
	}

	public String getCloseTag() {
		return "</" + getTagName() + ">";
	}

	@Override
	public void write(OutputStreamWriter o) throws IOException {
		o.write(getOpenTag());
		if (children != null)
			for (WebElement w : children)
				w.write(o);
		o.write(getCloseTag());
	}

}
