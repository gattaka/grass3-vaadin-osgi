package cz.gattserver.grass3.articles.services.mockjs;

import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class MockJSElement implements Element {

	private String content;

	public MockJSElement(String content) {
		this.content = content;
	}

	@Override
	public void apply(Context ctx) {
		ctx.addJSCode(content);
		ctx.addJSResource("pre-resource");
		ctx.print("JS-loaded");
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}

}
