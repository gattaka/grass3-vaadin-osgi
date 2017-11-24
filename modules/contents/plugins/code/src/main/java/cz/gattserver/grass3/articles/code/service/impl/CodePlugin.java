package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.AbstractCodePlugin;

@Component
public class CodePlugin extends AbstractCodePlugin {

	public CodePlugin() {
		super("CODE", "Code", "", null, null);
	}

}
