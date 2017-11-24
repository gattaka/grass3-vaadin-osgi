package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.AbstractCodePlugin;

@Component
public class PHPCodePlugin extends AbstractCodePlugin {

	public PHPCodePlugin() {
		super("PHP", "PHP", "php.png", "php", "php");
	}

}
