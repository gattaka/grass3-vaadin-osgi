package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.AbstractCodePlugin;

@Component
public class CSSCodePlugin extends AbstractCodePlugin {

	public CSSCodePlugin() {
		super("CSS", "CSS", "", "css", "css");
	}

}
