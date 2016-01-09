package cz.gattserver.grass3.wexp.out.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import cz.gattserver.grass3.wexp.out.WebElement;

public class Page {

	private static final String DOCTYPE_HEADER = "<!DOCTYPE html>";

	private String charset;

	private HtmlElement html = new HtmlElement();
	private HeadElement head = new HeadElement();
	private BodyElement body = new BodyElement();

	public Page(String charset) {
		this.charset = charset;
		html.addChild(head, body);
	}

	public Page addHeader(WebElement... childList) {
		head.addChild(childList);
		return this;
	}

	public Page addChild(WebElement... childList) {
		body.addChild(childList);
		return this;
	}

	public void write(OutputStream o) throws IOException {
		try {
			OutputStreamWriter writer = new OutputStreamWriter(o, charset);
			writer.write(DOCTYPE_HEADER);
			html.write(writer);
			writer.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
