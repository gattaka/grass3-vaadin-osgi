package cz.gattserver.grass3.articles.editor.parser;

import cz.gattserver.grass3.articles.editor.lexer.Token;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.elements.TextElement;

/**
 * @author gatt
 */
public interface Parser {

	/**
	 * Projde článek a vytvoří {@link Element}
	 * 
	 * @param pluginBag
	 *            objekt s daty, která je předáván mezi parser pluginy
	 * @return {@link Element} AST strom pro finální generování výsledného
	 *         článku
	 */
	Element parse(ParsingProcessor pluginBag);

	/**
	 * <p>
	 * Zjistí od parseru jestli se v jeho {@link TextElement} potomcích má při
	 * parsování projevit zalomení řádku jako HTML <code>&lt;br/&gt;</code> nebo
	 * přímo jako EOL znak <code>\n</code>.
	 * </p>
	 * 
	 * <p>
	 * <strong>Nijak neřídí</strong> dění parseru při vyžádání si textu jako
	 * takového, ten je nadále rozdělen lexerem při nalezení EOL na
	 * {@link Token#TEXT} a {@link Token#EOL}.
	 * </p>
	 * 
	 * @return {@code true} pokud je povolen v pluginu <br/>
	 */
	boolean canHoldBreaklineElement();

}
