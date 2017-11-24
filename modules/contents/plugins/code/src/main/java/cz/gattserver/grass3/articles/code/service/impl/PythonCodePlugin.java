package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.AbstractCodePlugin;

@Component
public class PythonCodePlugin extends AbstractCodePlugin {

	public PythonCodePlugin() {
		super("PYTHON", "Python", "python.png", "python", "python");
	}

}
