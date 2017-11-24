package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.AbstractCodePlugin;

@Component
public class SQLCodePlugin extends AbstractCodePlugin {

	public SQLCodePlugin() {
		super("SQL", "SQL", "", "sql", "sql");
	}

}
