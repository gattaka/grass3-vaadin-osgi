package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.AbstractCodePlugin;

@Component
public class BASHCodePlugin extends AbstractCodePlugin {

	public BASHCodePlugin() {
		super("BASH", "BASH", "bash.gif", "shell", "shell");
	}

}
