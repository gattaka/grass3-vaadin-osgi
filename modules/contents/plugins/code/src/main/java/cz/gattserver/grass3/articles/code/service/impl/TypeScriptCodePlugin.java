package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.AbstractCodePlugin;

@Component
public class TypeScriptCodePlugin extends AbstractCodePlugin {

	public TypeScriptCodePlugin() {
		super("TS", "TypeScript", "ts.png", "javascript", "ts");
	}

}
