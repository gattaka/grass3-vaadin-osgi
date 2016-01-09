package cz.gattserver.grass3.wexp.out.impl;

import java.io.IOException;
import java.io.OutputStreamWriter;

import cz.gattserver.grass3.wexp.out.WebElement;

public class TextElement extends WebElement {

	private String text;

	public TextElement(String text) {
		this.text = text;
	}

	@Override
	public void write(OutputStreamWriter o) throws IOException {
		o.write(text);
	}

}
