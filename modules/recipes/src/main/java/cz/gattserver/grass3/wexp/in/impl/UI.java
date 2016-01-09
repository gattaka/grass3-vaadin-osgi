package cz.gattserver.grass3.wexp.in.impl;

import java.util.HashMap;
import java.util.Map;

import cz.gattserver.grass3.wexp.in.Component;
import cz.gattserver.grass3.wexp.out.impl.Page;
import cz.gattserver.grass3.wexp.out.impl.StyleElement;
import cz.gattserver.grass3.wexp.out.impl.TextElement;

public class UI {

	protected Component content;

	protected Map<String, CSSRule> css;

	private String charset = "UTF-8";

	public UI setClass(CSSRule rule) {
		if (css == null)
			css = new HashMap<String, CSSRule>();
		css.put(rule.getName(), rule);
		return this;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public UI setContent(Component content) {
		this.content = content;
		return this;
	}

	public Page construct() {
		Page page = new Page(charset);
		if (css != null) {
			StyleElement style = new StyleElement();
			String text = "";
			for (String cssRuleKey : css.keySet()) {
				CSSRule c = css.get(cssRuleKey);
				text += c.getName() + " { ";
				for (String key : c.getStyles().keySet()) {
					text += key + ": " + c.getStyles().get(key) + ";";
				}
				text += " } ";
			}
			TextElement cssText = new TextElement(text);
			style.addChild(cssText);
			page.addHeader(style);
		}
		page.addChild(content.constructWithStyles());
		return page;
	}

}
