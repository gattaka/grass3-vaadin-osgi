package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.AbstractCodePlugin;

@Component
public class LatexCodePlugin extends AbstractCodePlugin {

	public LatexCodePlugin() {
		super("LATEX", "LaTeX", "", "stex", "latex");
	}

}
