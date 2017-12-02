package cz.gattserver.grass3.articles.editor.parser;

import static cz.gattserver.grass3.articles.editor.lexer.Token.*;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.lexer.Token;
import cz.gattserver.grass3.articles.editor.parser.elements.BreaklineElement;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.elements.ParserErrorElement;
import cz.gattserver.grass3.articles.editor.parser.elements.PluginErrorElement;
import cz.gattserver.grass3.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass3.articles.editor.parser.exceptions.ParserException;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass3.articles.editor.parser.interfaces.PositionTO;
import cz.gattserver.grass3.articles.editor.parser.util.HTMLEscaper;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * <p>
 * Každý {@link Parser} v sobě může mít obsahy jiných {@link Plugin}ů, proto je
 * potřeba aby měl možnost si zavolat o překlad nějaké části svého obsahu jiný
 * plugin.
 * </p>
 * <p>
 * Protože ale rozhodování, který plugin na co zavolat náleží centrálnímu
 * parseru, je to řešené takto pomocí předávání tohoto objektu, který volá
 * pluginy na základně žádností provádějících pluginů, plus si drží informace
 * jako pozice v textu apod.
 * </p>
 * 
 * @author gatt
 */
public class ParsingProcessor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Map<String, Plugin> registerSnapshot;
	private Token token;
	private Lexer lexer;

	/**
	 * některé pluginy potřebují sázet linky a u těch je občas potřeba znát
	 * kořenovou adresu
	 */
	private String contextRoot;

	/**
	 * zásobník aktivovaných pluginů - dalo by se to řešit automaticky pomocí
	 * předávání instancí {@link ParsingProcessor}, ale to má stejný význam,
	 * navíc to ještě na zásobník (systémový) ukládá kvantum věcí navíc, tohle
	 * je úspornější
	 */
	private Stack<StackElement> activePlugins;

	/**
	 * položka stacku
	 */
	private class StackElement {

		/**
		 * Jaký tag se právě řeší
		 */
		private String tag;

		/**
		 * Jeho parser
		 */
		private Parser parserPlugin;

		public Parser getParserPlugin() {
			return parserPlugin;
		}

		public String getTag() {
			return tag;
		}

		private StackElement(Parser parserPlugin, String tag) {
			this.tag = tag;
			this.parserPlugin = parserPlugin;
		}

		@Override
		public String toString() {
			return tag;
		}

	}

	public ParsingProcessor(Lexer lexer, String contextRoot, Map<String, Plugin> registerSnapshot) {
		this.lexer = lexer;
		this.contextRoot = contextRoot;
		this.activePlugins = new Stack<StackElement>();
		this.registerSnapshot = registerSnapshot;
	}

	/**
	 * Pro pluginy, které pracují s linky a obrázky je potřeba občas uvádět
	 * kořen webu, ke kterému potom budou
	 * 
	 * @return kořen webu
	 */
	public String getContextRoot() {
		return contextRoot;
	}

	/**
	 * Zjistí jaký token se právě zpracovává
	 * 
	 * @return {@link Token } dle toho, co právě našel
	 * @see {@link Token }
	 */
	public Token getToken() {
		return token;
	}

	/**
	 * Zpracuje další {@link Token }
	 */
	public void nextToken() {
		this.token = lexer.nextToken();
	}

	public PositionTO getPosition() {
		return lexer.getPosition();
	}

	/**
	 * Vrátí naparsovaný obsah jako plain-text
	 * 
	 * @return text
	 */
	public String getCode() {
		return lexer.getText();
	}

	/**
	 * Vrátí naparsovaný obsah jako escapovaný plain-text
	 * 
	 * @return text
	 */
	public String getText() {
		return HTMLEscaper.stringToHTMLString(getCode());
	}

	/**
	 * Vrátí naparsovaný obsah jako by to byl počáteční tag - odstřihne od něj
	 * tedy počáteční a koncovou hranatou závorku
	 * 
	 * @return počáteční tag
	 * @throws TokenException
	 *             pokud není akutální token {@link Token#START_TAG}
	 */
	public String getStartTag() {
		if (!Token.START_TAG.equals(token))
			throw new TokenException(Token.START_TAG, token, lexer.getText());
		return lexer.getStartTag();
	}

	/**
	 * Vrátí naparsovaný obsah jako by to byl koncový tag - odstřihne od něj
	 * tedy počáteční a koncovou hranatou závorku a lomítko
	 * 
	 * @return koncový tag
	 * @throws TokenException
	 *             pokud není akutální token {@link Token#END_TAG}
	 */
	public String getEndTag() {
		if (!Token.END_TAG.equals(token))
			throw new TokenException(Token.END_TAG, token, lexer.getText());
		return lexer.getEndTag();
	}

	/**
	 * ParserCore
	 */
	private Element parseTag() {
		String tag = getStartTag();
		logger.info("Looking for the right ParserPlugin for tag '" + tag + "'");

		Plugin plugin = registerSnapshot.get(tag);
		if (plugin != null) {
			Parser parser = plugin.getParser();
			try {
				// vstupuješ do dalšího patra parsovacího stromu
				// => nastav si, že tento plugin je právě u prohledávání
				activePlugins.push(new StackElement(parser, tag));
				logger.info(parser.getClass() + " was pushed in stack and launched");
				logger.info("activePlugins: " + activePlugins);

				// Spusť plugin
				Element elementTree = parser.parse(this);

				parser = activePlugins.pop().getParserPlugin();
				logger.info(parser.getClass() + " terminates (clean) and was poped from stack");
				logger.info("activePlugins: " + activePlugins);

				return elementTree;
			} catch (TokenException ex) {
				// Plugin běží, ale je problém s očekávanou posloupností Tokenů
				parser = activePlugins.pop().getParserPlugin();
				logger.info(parser.getClass() + " terminates (token exception) and was poped from stack");
				logger.info("activePlugins: " + activePlugins);
				return new ParserErrorElement(tag, ex.toString(), activePlugins.toString());
			} catch (ParserException pe) {
				// Plugin běží, ale došlo v něm k nějaké jiné chybě
				parser = activePlugins.pop().getParserPlugin();
				logger.warn(parser.getClass() + " terminates (parse exception) and was poped from stack");
				logger.warn("activePlugins: " + activePlugins);
				return new ParserErrorElement(tag, pe.getMessage(), activePlugins.toString());
			} catch (Exception ex) {
				// Došlo k chybě
				parser = activePlugins.pop().getParserPlugin();
				logger.error(parser.getClass() + " terminates (plugin exception) and was poped from stack");
				logger.error("activePlugins: " + activePlugins);
				logger.error("Plugin error", ex);
				return new PluginErrorElement(tag);
			}
		} else {
			logger.info("ParserPlugin for tag '" + tag + "' not found, '" + tag + "' is text");
		}

		return null; // jinak vrať null - žádný plugin tohle nezná
	}

	/**
	 * Zpracuje obsah jako podstrom prvků. Parsuje, dokud nenarazí na volný
	 * {@link Token#END_TAG}, tedy ukončovací tag, který nebyl zpracován v rámci
	 * párování tagů prvků podstromu.
	 * 
	 * @param elist
	 *            list do kterého se budou ukládat výsledné podstromy prvků
	 */
	public void getBlock(List<Element> elist) {
		logger.info("block: " + getToken());
		switch (getToken()) {
		case START_TAG:
		case TEXT:
		case TAB:
		case EOL:
			elist.add(getElement());
			getBlock(elist);
			break;
		case END_TAG:
			// je aktivní nějaký plugin nebo parsuju kořenový blok článku?
			if (!activePlugins.empty()) {
				// pokud je ativní nějaký plugin, pak čeká na svůj ukončovací
				// token -- zkontroluj, zda je jeho
				String expectedEndTag = activePlugins.peek().getTag();
				String actualEndTag = getEndTag();
				if (expectedEndTag.equals(actualEndTag)) {
					// je jeho, ukončil jsem v pořádku parsování jeho obsahu
					// jako podstrom prvků, ukonči blok
					break;
				} else {
					boolean isInActive = activePlugins.contains(actualEndTag);
					// není jeho -- jde o ukončovací tag některého z aktivních
					// pluginů? Pokud ano, pak to ber jako chybu. Pokud ne, pak
					// ho ber jako text a parsuj obsah dál, jako prvky jeho
					// podstromu.
					// Tím je umožněno, aby se dalo napsat například
					// [N1][/TEST][/N1], ale zároveň aby se dali lokalizovat
					// chyby
					if (isInActive)
						throw new TokenException(expectedEndTag, actualEndTag);
				}
			}
			elist.add(getTextTree());
			getBlock(elist);
			break;
		case EOF:
		default:
			if (!activePlugins.isEmpty())
				throw new TokenException(Token.END_TAG, activePlugins.peek().getTag());
			break;
		}
	}

	private BreaklineElement getBreakline() {
		logger.info("breakline: " + getToken());
		nextToken();
		return new BreaklineElement();
	}

	/**
	 * Zpracuje element
	 * 
	 * @return
	 */
	public Element getElement() {
		logger.info("element: " + getToken());
		switch (getToken()) {
		case START_TAG:
		case END_TAG:
			Element element = parseTag();
			return element == null ? getTextTree() : element;
		case TEXT:
		case TAB:
			return getTextTree();
		case EOL:
			return getBreakline();
		case EOF:
		default:
			logger.warn("Čekal jsem jeden z [" + new Token[] { START_TAG, END_TAG, TEXT, EOL } + "], ne " + getToken()
					+ "%n");
			throw new ParserException();
		}
	}

	/**
	 * Zpracuje obsah jako obyčejý text - nebezpečné znaky zaescapuje
	 * 
	 * @return escapovaný text TextTree AST
	 */
	public TextElement getTextTree() {
		// většinou chci aby znaky byly escapovány
		return getTextTree(true);
	}

	private TextElement getTextTree(boolean escaped) {
		logger.info("text: " + getToken());
		String text = escaped ? getText() : getCode();
		nextToken();
		TextElement element = new TextElement(text);
		return element;
	}

	/**
	 * Zpracuje obsah jako by to byl kód - nebude escapovat obsah
	 * 
	 * @return neescapovaný {@link TextElement} AST
	 */
	public TextElement getCodeTextTree() {
		// ... to je taky text, ale neescapován
		return getTextTree(false);
	}
}
