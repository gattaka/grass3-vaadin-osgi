package cz.gattserver.grass3.articles.plugins.basic.style.align;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStyleParser;

public class RightAlignPluginTest {

	@Test
	public void testProperties() {
		RightAlignPlugin plugin = new RightAlignPlugin();
		assertEquals("ALGNRT", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertTrue(to.getImage() instanceof ThemeResource);
		assertEquals("articles/basic/img/algnr_16.png", ((ThemeResource) to.getImage()).getResourceId());
		assertEquals("[ALGNRT]", to.getPrefix());
		assertEquals("[/ALGNRT]", to.getSuffix());
		assertEquals("ALGNRT", to.getTag());
		assertEquals("Zarovnání", to.getTagFamily());
	}

}
