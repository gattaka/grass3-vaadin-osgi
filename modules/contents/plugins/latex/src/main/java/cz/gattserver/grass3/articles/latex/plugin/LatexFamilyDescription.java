package cz.gattserver.grass3.articles.latex.plugin;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.plugins.PluginFamilyDescription;

/**
 * @author gatt
 */
@Component
public class LatexFamilyDescription implements PluginFamilyDescription {

	@Override
	public String getFamily() {
		return "LaTeX";
	}

	@Override
	public String getDescription() {
		return "(<a target=\"_blank\" href=\"https://katex.org/docs/supported.html\">info</a>)";
	}

}
