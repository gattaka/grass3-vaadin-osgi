package cz.gattserver.grass3.articles.latex.plugin;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
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
			protected String decorateFormula(String formula) {
				// rovnice musí mít nastaveno prostředí aligned
				return "\\begin{aligned} " + super.decorateFormula(formula)
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
