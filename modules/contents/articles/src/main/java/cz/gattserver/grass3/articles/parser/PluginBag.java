package cz.gattserver.grass3.articles.parser;

import static cz.gattserver.grass3.articles.lexer.Token.*;

import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.lexer.Lexer;
import cz.gattserver.grass3.articles.lexer.Token;
import cz.gattserver.grass3.articles.parser.elements.BreaklineTree;
import cz.gattserver.grass3.articles.parser.elements.ParserError;
import cz.gattserver.grass3.articles.parser.elements.PluginError;
import cz.gattserver.grass3.articles.parser.elements.TextTree;
import cz.gattserver.grass3.articles.parser.exceptions.ParserException;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import cz.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import cz.gattserver.grass3.articles.parser.misc.HTMLEscaper;

/**
 * Propojovací třída pluginů a jejich parserů - zajišťuje, že když si Parser
 * předá "kontext" mezi sebou a ParserPluginem, že ParserPlugin bude pracovat s
 * daty, jejichž změny pak rovnou uvidí původní Parser.
 * 
 * Každý parser v sobě může mít obsahy jiných pluginů, proto je potřeba aby měl
 * možnost si zavolat o překlad nějaké části svého obsahu jiný plugin. Protože
 * ale rozhodování, který plugin na co zavolat náleží centrálnímu parseru, je to
 * řešené takto pomocí předávání tohoto objektu, který volá pluginy na základně
 * žádností provádějících pluginů, plus si drží informace jako pozice v textu
 * apod.
 * 
 * @author gatt
 */
public class PluginBag {

	private Token token;
	private Lexer lexer;
	private PluginRegister pluginRegister;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private void log(String log) {
		logger.info(log);
	}

	/**
	 * některé pluginy potřebují sázet linky a u těch je občas potřeba znát
	 * kořenovou adresu
	 */
	private String contextRoot;

	/**
	 * zásobník aktivovaných pluginů - dalo by se to řešit automaticky pomocí
	 * předávání instancí PluginBag, ale to má stejný význam, navíc to ještě na
	 * zásobník (systémový) ukládá kvantum věcí navíc, tohle je úspornější
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
		private AbstractParserPlugin parserPlugin;

		public AbstractParserPlugin getParserPlugin() {
			return parserPlugin;
		}

		public String getTag() {
			return tag;
		}

		private StackElement(AbstractParserPlugin parserPlugin, String tag) {
			this.tag = tag;
			this.parserPlugin = parserPlugin;
		}

	}

	/**
	 * Vrátí, zda aktuální prováděný parser může mít v sobě znaky konce řádku
	 * jako {@code <br/>}
	 * 
	 * @return {@code true }, pokud lze vypisovat znak {@code <br/>} jinak
	 */
	public boolean canHoldBreakline() {
		// pokud to teď řídí nějaký plugin, tak vrať jeho rozhodnutí,
		// jinak pokud jsme pod základním Parserem, tak tam se může všechno
		return activePlugins.empty() ? true : activePlugins.peek().parserPlugin.canHoldBreakline();
	}

	public PluginBag(Lexer lexer, String contextRoot, PluginRegister pluginRegister) {
		this.lexer = lexer;
		this.contextRoot = contextRoot;
		this.pluginRegister = pluginRegister;
		this.activePlugins = new Stack<StackElement>();
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

	public Position getPosition() {
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
	 */
	public String getStartTag() {
		return lexer.getStartTag();
	}

	/**
	 * Vrátí naparsovaný obsah jako by to byl koncový tag - odstřihne od něj
	 * tedy počáteční a koncovou hranatou závorku a lomítko
	 * 
	 * @return koncový tag
	 */
	public String getEndTag() {
		return lexer.getEndTag();
	}

	/**
	 * ParserCore
	 */
	private AbstractElementTree parseTag() {

		String tag = getStartTag();

		log(this.getClass().getSimpleName() + ": Looking for the right ParserPlugin for tag '" + tag + "'");

		IPluginFactory pluginFactory = pluginRegister.get(tag);

		if (pluginFactory != null) {

			AbstractParserPlugin parserPlugin = pluginFactory.getPluginParser();

			try {
				try {

					// vstupuješ do dalšího patra parsovacího stromu
					// => nastav si, že tento plugin je právě u prohledávání
					activePlugins.push(new StackElement(parserPlugin, tag));
					log(this.getClass().getSimpleName() + ": " + parserPlugin.getClass().getCanonicalName()
							+ " was pushed in stack and launched");
					log(this.getClass().getSimpleName() + ": activePlugins: " + activePlugins.size());

					Position beginPosition = getPosition();

					// Spusť plugin
					AbstractElementTree elementTree = parserPlugin.parse(this);

					Position endPosition = getPosition();
					elementTree.setStartPosition(beginPosition);
					elementTree.setEndPosition(endPosition);

					parserPlugin = activePlugins.pop().getParserPlugin();
					log(this.getClass().getSimpleName() + ": " + parserPlugin.getClass().getCanonicalName()
							+ " terminates (clean) and was poped from stack");
					log(this.getClass().getSimpleName() + ": activePlugins: " + activePlugins.size());

					return elementTree;

				} catch (ParserException pe) {
					// Plugin běží, ale něco se mu nelíbí a tak vyhodil výjimku
					parserPlugin = activePlugins.pop().getParserPlugin();
					log(this.getClass().getSimpleName() + ": " + parserPlugin.getClass().getCanonicalName()
							+ " terminates (parse error) and was poped from stack");
					log(this.getClass().getSimpleName() + ": activePlugins: " + activePlugins.size());
					return new ParserError(tag);
				}
			} catch (Exception ex) {
				// V pluginu došlo k chybě
				parserPlugin = activePlugins.pop().getParserPlugin();
				log(this.getClass().getSimpleName() + ": " + parserPlugin.getClass().getCanonicalName()
						+ " terminates (plugin error) and was poped from stack");
				log(this.getClass().getSimpleName() + ": activePlugins: " + activePlugins.size());
				logger.error("Plugin error", ex);
				return new PluginError(tag);
			}
		}

		return null; // jinak vrať null - žádný plugin tohle nezná
	}

	/**
	 * Zpracuje obsah jako sadu elementů skládajích se z čehokoliv. Výsledné AST
	 * stromy přidá do předaného listu
	 * 
	 * @param elist
	 *            list do kterého se mají ukládat výsledné AST jednotlivých
	 *            elementů a textu
	 */
	public void getBlock(List<AbstractElementTree> elist) {
		log(this.getClass().getSimpleName() + ": block: " + getToken());
		switch (getToken()) {
		case START_TAG:
		case TEXT:
		case EOL:
			elist.add(getElement());
			getBlock(elist);
			break;

		case END_TAG:
			/**
			 * Ukončovací tag znamená:
			 * 
			 * a) konec bloku nějakého tagu
			 * 
			 * špatně se ověřuje, protože pokud je block volán z pluginu, tak se
			 * plugin se spoléhá na to, že block se tady ukončí a plugin si pak
			 * vyzvedne END_TAG sám a také se ukončí.
			 * 
			 * může se ale tady stát (a stalo se), že ale žádný plugin venku
			 * nečeká a block tak vlastně ukončí parsování jenom protože našel
			 * koncový tag nějakého existujícího pluginu
			 * 
			 * je proto nutné si nějak evidovat, zda někdo vyzvedne po ukončení
			 * tag nebo ne - pokud nikdo nečeká na vyzvednutí bude se END_TAG
			 * existujícího pluginu brát jako chyba nebo jako text, pokud plugin
			 * čeká až block skončí, bude se to ignorovat a provede se normální
			 * ukončení na break
			 * 
			 * b) text - není to tag, ale jen text
			 * 
			 * dá se ověřit tak, že pokud žádný plugin tenhle tag nezná tak je
			 * to text ...
			 * 
			 **/
			// pokud jsem ve volání a narazil jsem na ukončovací tag
			// volajícího pluginu, tak to ukonči
			if (!activePlugins.empty()) {
				if (activePlugins.peek().getTag().equals(getEndTag())) {
					break;
				}
			}

			// pokud nejsi ve volání, musíš endTag zpracovat jako text
			// pokud jsi ve volání, ale tento tag není známý, je to text
			elist.add(getTextTree());
			getBlock(elist);
			break;
		case EOF:
		default:
			// konec kaskády
			break;
		}
	}

	private BreaklineTree getBreakline() {
		log(this.getClass().getSimpleName() + ": breakline: " + getToken());
		switch (getToken()) {
		case EOL:
			// pokud je povolené odřádkování, tak se vloží <br/> jinak ' '
			nextToken();
			return new BreaklineTree(canHoldBreakline());
		default:
			log("Čekal jsem: " + EOL + ", ne " + getToken() + "%n");
			throw new ParserException();
		}
	}

	/**
	 * Zpracuje element
	 * 
	 * @return
	 */
	public AbstractElementTree getElement() {
		log(this.getClass().getSimpleName() + ": element: " + getToken());
		switch (getToken()) {
		case START_TAG:
		case END_TAG:
			AbstractElementTree element = parseTag();
			return element == null ? getTextTree() : element;
		case TEXT:
			return getTextTree();
		case EOL:
			return getBreakline();
		case EOF:
		default:
			log("Čekal jsem: " + START_TAG + ", " + END_TAG + ", " + TEXT + " nebo " + EOL + ", ne " + getToken()
					+ "%n");
			throw new ParserException();
		}
	}

	/**
	 * Zpracuje obsah jako obyčejý text - nebezpečné znaky zaescapuje
	 * 
	 * @return escapovaný text TextTree AST
	 */
	public TextTree getTextTree() {
		// většinou chci aby znaky byly escapovány
		return getTextTree(true);
	}

	private TextTree getTextTree(boolean escaped) {
		log(this.getClass().getSimpleName() + ": text: " + getToken());

		Position beginPosition = getPosition();
		String text = escaped ? getText() : getCode();
		nextToken();
		TextTree element = new TextTree(text);
		Position endPosition = getPosition();
		element.setStartPosition(beginPosition);
		element.setEndPosition(endPosition);
		return element;
	}

	/**
	 * Zpracuje obsah jako by to byl kód - nebude escapovat obsah
	 * 
	 * @return neescapovaný {@link TextTree} AST
	 */
	public TextTree getCodeTextTree() {
		// ... to je taky text, ale neescapován
		return getTextTree(false);
	}
}
