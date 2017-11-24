package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.AbstractCodePlugin;

@Component
public class CSharpCodePlugin extends AbstractCodePlugin {

	public CSharpCodePlugin() {
		super("CSHARP", "C#", "", "clike", "csharp");
	}

}
