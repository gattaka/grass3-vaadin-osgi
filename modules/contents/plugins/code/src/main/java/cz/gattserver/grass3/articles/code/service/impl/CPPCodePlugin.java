package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.AbstractCodePlugin;

@Component
public class CPPCodePlugin extends AbstractCodePlugin {

	public CPPCodePlugin() {
		super("CPP", "C++", "", "clike", "cpp");
	}
}
