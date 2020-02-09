package cz.gattserver.grass3.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class CSharpCodePlugin extends AbstractCodePlugin {

	public CSharpCodePlugin() {
		super("CSHARP", "C#", "", "clike", "csharp");
	}

}
