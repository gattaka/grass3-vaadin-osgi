package cz.gattserver.grass3.articles.templates.plotter;

import org.apache.commons.lang3.StringUtils;

import cz.gattserver.grass3.articles.editor.lexer.Token;
import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.ParserException;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;

/**
 * @author gatt
 */
public class PlotterParser implements Parser {

	private static final String FORMAT_ERROR = "Invalid plotter format. Expected: function;x1;x2;y1;y2";

	private String tag;

	public PlotterParser(String tag) {
		this.tag = tag;
	}

	@Override
	public Element parse(ParsingProcessor processor) {
		parseStartTag(processor, tag);

		String text = parseText(processor);
		if (StringUtils.isBlank(text))
			throw new ParserException(FORMAT_ERROR);

		String[] values = text.split(";");
		if (values.length != 5)
			throw new ParserException(FORMAT_ERROR);

		String function = values[0];
		if (!function.contains("x"))
			throw new ParserException("Plotter function variable must be 'x'");

		double startx = parseDoubleNumber(values[1], "'x1'");
		double endx = parseDoubleNumber(values[2], "'x2'");
		double starty = parseDoubleNumber(values[3], "'y1'");
		double endy = parseDoubleNumber(values[4], "'y2'");

		// zpracovat koncový tag
		parseEndTag(processor, tag);

		return new PlotterElement(startx, endx, starty, endy, function);
	}

	private void parseStartTag(ParsingProcessor processor, String tag) {
		String startTag = processor.getStartTag();
		if (!startTag.equals(tag))
			throw new TokenException(tag, startTag);
		processor.nextToken();
	}

	private void parseEndTag(ParsingProcessor processor, String tag) {
		String endTag = processor.getEndTag();
		if (!endTag.equals(tag))
			throw new TokenException(tag, processor.getCode());
		processor.nextToken();
	}

	private String parseText(ParsingProcessor processor) {
		String text;
		if (processor.getToken() != Token.EOF) {
			text = processor.getText();
		} else {
			throw new TokenException(Token.TEXT);
		}
		processor.nextToken();
		return text;
	}

	private double parseDoubleNumber(String text, String name) {
		try {
			return Double.parseDouble(text);
		} catch (NumberFormatException e) {
			throw new ParserException("Invalid double for " + name);
		}
	}

}
