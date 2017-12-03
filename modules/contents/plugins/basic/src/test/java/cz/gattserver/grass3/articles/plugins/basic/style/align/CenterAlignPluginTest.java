package cz.gattserver.grass3.articles.plugins.basic.style.align;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStyleParser;

public class CenterAlignPluginTest {

	@Test
	public void testProperties() {
		CenterAlignPlugin plugin = new CenterAlignPlugin();
		assertEquals("ALGNCT", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertTrue(to.getImage() instanceof ThemeResource);
		assertEquals("articles/basic/img/algnc_16.png", ((ThemeResource) to.getImage()).getResourceId());
		assertEquals("[ALGNCT]", to.getPrefix());
		assertEquals("[/ALGNCT]", to.getSuffix());
		assertEquals("ALGNCT", to.getTag());
		assertEquals("Zarovnání", to.getTagFamily());
	}

}
