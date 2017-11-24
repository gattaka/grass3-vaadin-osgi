package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.AbstractCodePlugin;

@Component
public class CCodePlugin extends AbstractCodePlugin {

	public CCodePlugin() {
		super("C", "C", "", "clike", "c");
	}
}
