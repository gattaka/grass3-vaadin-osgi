package cz.gattserver.grass3.articles.templates.plotter;

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

	private String tag;
	private String startXTag;
	private String endXTag;
	private String startYTag;
	private String endYTag;
	private String funcTag;

	private double startx;
	private double endx;
	private double starty;
	private double endy;
	private String function;

	public PlotterParser(String tag) {
		this.tag = tag;
	}

	public PlotterParser(String tag, String startXTag, String endXTag, String startYTag, String endYTag,
			String funcTag) {
		this.tag = tag;
		this.startXTag = startXTag;
		this.endXTag = endXTag;
		this.startYTag = startYTag;
		this.endYTag = endYTag;
		this.funcTag = funcTag;
	}

	@Override
	public Element parse(ParsingProcessor processor) {
		parseStartTag(processor, tag);

		parseStartTag(processor, startXTag);
		startx = parseDoubleNumber(processor);
		parseEndTag(processor, startXTag);

		parseStartTag(processor, endXTag);
		endx = parseDoubleNumber(processor);
		parseEndTag(processor, endXTag);
		
		parseStartTag(processor, startYTag);
		starty = parseDoubleNumber(processor);
		parseEndTag(processor, startYTag);

		parseStartTag(processor, endYTag);
		endy = parseDoubleNumber(processor);
		parseEndTag(processor, endYTag);

		parseStartTag(processor, funcTag);
		function = parseText(processor);
		parseEndTag(processor, funcTag);

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

	private Double parseDoubleNumber(ParsingProcessor processor) {
		Double number;
		if (processor.getToken() != Token.EOF) {
			try {
				number = Double.parseDouble(processor.getText());
			} catch (NumberFormatException e) {
				throw new ParserException(e);
			}
		} else {
			throw new TokenException(Token.TEXT);
		}
		processor.nextToken();
		return number;
	}

}
