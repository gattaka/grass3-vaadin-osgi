package org.myftp.gattserver.grass3.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;

public class JQueryAccordion extends CustomLayout {

	private static final long serialVersionUID = 6234687236886911109L;

	private final static String LOCATION_BASE_NAME = "elementContent";

	private int elementsFilled = 0;

	private static String createLayoutMarkup(Collection<String> headerNames) {
		StringBuilder builder = new StringBuilder();

		builder.append("<div id=\"accordion\">");

		int counter = 0;
		for (String header : headerNames) {
			builder.append("<h3>" + header + "</h3><div location=\""
					+ LOCATION_BASE_NAME + counter + "\"></div>");
			counter++;
		}

		builder.append("</div>");

		return builder.toString();
	}

	private static InputStream createStream(String markup) {
		try {
			return new ByteArrayInputStream(markup.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public JQueryAccordion(String... headerNames) throws IOException {
		super(createStream(createLayoutMarkup(Arrays.asList(headerNames))));
	}

	public JQueryAccordion(Collection<String> headerNames) throws IOException {
		super(createStream(createLayoutMarkup(headerNames)));
	}

	/**
	 * Lze použít k přepsání již nasetovaného elementu
	 * 
	 * @param component
	 * @param index
	 */
	public void setElement(Component component, int index) {
		addComponent(component, LOCATION_BASE_NAME + index);
	}

	/**
	 * Automaticky setuje postupně všechny elementy
	 * 
	 * @param component
	 */
	public void setNextElement(Component component) {
		addComponent(component, LOCATION_BASE_NAME + elementsFilled);
		elementsFilled++;
	}

}
