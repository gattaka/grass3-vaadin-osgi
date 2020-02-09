package cz.gattserver.grass3.articles.services.mock;

import java.util.stream.IntStream;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class MockElement implements Element {

	private int numberOfStars;

	public MockElement(int numberOfStars) {
		this.numberOfStars = numberOfStars;
	}

	@Override
	public void apply(Context ctx) {
		ctx.print("<span>");
		IntStream.range(0, numberOfStars).forEach(i -> ctx.print("*"));
		ctx.print("</span>");
	}

}
