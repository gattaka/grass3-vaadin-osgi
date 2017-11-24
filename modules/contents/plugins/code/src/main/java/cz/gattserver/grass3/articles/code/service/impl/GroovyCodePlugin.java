package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.AbstractCodePlugin;

@Component
public class GroovyCodePlugin extends AbstractCodePlugin {

	public GroovyCodePlugin() {
		super("GROOVY", "Groovy", "groovy.jpeg", "groovy", "groovy");
	}

}
