package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.AbstractCodePlugin;

@Component
public class XMLCodePlugin extends AbstractCodePlugin {

	public XMLCodePlugin() {
		super("XML", "HTML/XML", "", "xml", "xml");
	}

}
