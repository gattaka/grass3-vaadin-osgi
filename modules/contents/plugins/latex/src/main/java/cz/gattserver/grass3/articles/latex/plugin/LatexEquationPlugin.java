package cz.gattserver.grass3.articles.latex.plugin;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.lexer.Token;
import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
@Component
public class LatexEquationPlugin implements Plugin {

	private final String tag = "EQ";

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new LatexParser(tag) {

			@Override
			protected String processFormula(ParsingProcessor pluginBag) {
				StringBuilder formulaBuilder = new StringBuilder();
				/**
				 * Zpracuje vnitřek tagů jako code - jedu dokud nenarazím na
				 * svůj koncový tag - všechno ostatní beru jako obsah latex
				 * zdrojáku - text i potenciální počáteční tagy. Jediná věc,
				 * která mne může zastavit je EOF nebo můj koncový tag.
				 */
				Token currentToken = null;
				while (true) {
					currentToken = pluginBag.getToken();
					if ((currentToken == Token.END_TAG && pluginBag.getEndTag().equals(tag))
							|| currentToken == Token.EOF)
						break;
					if (Token.EOL.equals(currentToken)) {
						formulaBuilder.append('\n');
					} else {
						formulaBuilder.append(pluginBag.getCode());
					}
					pluginBag.nextToken();
				}

				String formula = formulaBuilder.toString();
				// rovnice musí mít nastaveno prostředí aligned
				return "\\begin{aligned} " + formula
						// aby zarovnání rovnic fungovalo, musí se k
						// rovnítkům dát znak &, aby bylo jasné, která
						// strana zůstane a která se bude posouvat pro
						// zarovnání. Pokud už v kódu '&=' použito je,
						// je potřeba ho dočasně změnit na '=', aby se
						// při nahrazení nevytvořili duplicitní '&&='
						.replaceAll("&=", "=").replaceAll("=", "&=")
						// Zarovnané rovnice musí mít oddělovač řádků \\
						.replaceAll("\n", " \\\\\\\\ ") + "\\end{aligned}";
			}
		};
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(tag, "LaTeX").setDescription("LaTeX equation").build();
	}

}
