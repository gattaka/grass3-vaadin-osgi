package cz.gattserver.grass3.articles.jslibs;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass3.articles.jslibs.matrix.ColorsElement;

/**
 * @author gatt
 */
public class GJSLibParser implements Parser {

	private String tag;

	public GJSLibParser(String tag) {
		this.tag = tag;
	}

	@Override
	public Element parse(ParsingProcessor processor) {
		parseStartTag(processor, tag);

		// zpracovat koncový tag
		parseEndTag(processor, tag);

		return new ColorsElement();
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
}
