package cz.gattserver.grass3.articles.editor.parser.exceptions;

/**
 * Obecná výjimka pro hlášení chyby během parsování
 * 
 * @author Hynek
 *
 */
public class ParserException extends RuntimeException {

	private static final long serialVersionUID = -4168431404585234070L;

	public ParserException(Throwable cause) {
		super(cause);
	}

	public ParserException() {
	}
}
