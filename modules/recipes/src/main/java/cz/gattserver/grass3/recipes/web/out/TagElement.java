package cz.gattserver.grass3.recipes.web.out;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TagElement extends WebElement {

	private static final String STYLE_ATTRIBUTE = "style";
	private static final String CLASS_ATTRIBUTE = "class";

	protected abstract String getTagName();

	protected List<WebElement> children;

	protected Map<String, String> attributes;
	protected Map<String, String> styles;
	protected List<String> classes;

	public TagElement addChild(WebElement... childList) {
		if (children == null)
			children = new ArrayList<WebElement>();
		for (WebElement child : childList)
			children.add(child);
		return this;
	}

	public TagElement setStyle(String name, String value) {
		if (styles == null)
			styles = new HashMap<String, String>();
		styles.put(name, value);
		return this;
	}

	public TagElement setClass(String name) {
		if (classes == null)
			classes = new ArrayList<String>();
		classes.add(name);
		return this;
	}

	public TagElement setAttribute(String name, String value) {
		if (attributes == null)
			attributes = new HashMap<String, String>();
		attributes.put(name, value);
		return this;
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
