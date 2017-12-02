package cz.gattserver.grass3.articles.editor.parser.exceptions;

import org.apache.commons.lang3.Validate;

import cz.gattserver.common.util.StringPreviewCreator;
import cz.gattserver.grass3.articles.editor.lexer.Token;

/**
 * Výjimka přesně pro případy, kdy byl očekáván nějaký {@link Token} a místo
 * toho byla nalezen jiný
 * 
 * @author Hynek
 *
 */
public class TokenException extends RuntimeException {

	private static final long serialVersionUID = -4168431404585234070L;

	private final String message;

	public TokenException(Token expected, Token actual, String actualContent) {
		Validate.notNull(expected, "Expected Token nesmí být null");
		Validate.notNull(expected, "Actual Token nesmí být null");
		this.message = "Expected Token: " + expected + " Actual Token: " + actual + " ("
				+ StringPreviewCreator.createPreview(actualContent, 20) + ")";
	}

	public TokenException(String expectedContent, String actualContent) {
		Validate.notNull(expectedContent, "Expected content nesmí být null");
		Validate.notNull(actualContent, "Actual content nesmí být null");
		this.message = "Expected content: '" + expectedContent + "' Actual content: "
				+ StringPreviewCreator.createPreview(actualContent, 20);
	}

	public TokenException(Token expected, String tag) {
		Validate.notNull(expected, "Expected Token nesmí být null");
		this.message = "Expected Token: " + expected + " (" + tag + ") Actual Token: " + Token.EOF;
	}
	
	public TokenException(Token expected) {
		Validate.notNull(expected, "Expected Token nesmí být null");
		this.message = "Expected Token: " + expected + " Actual Token: " + Token.EOF;
	}

	@Override
	public String toString() {
		return message;
	}
}
