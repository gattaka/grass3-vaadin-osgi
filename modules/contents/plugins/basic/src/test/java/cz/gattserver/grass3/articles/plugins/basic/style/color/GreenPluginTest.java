package cz.gattserver.grass3.articles.plugins.basic.style.color;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStyleParser;

public class GreenPluginTest {

	@Test
	public void testProperties() {
		GreenPlugin plugin = new GreenPlugin();
		assertEquals("GRN", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Zeleně", to.getDescription());
		assertNull(to.getImage());
		assertEquals("[GRN]", to.getPrefix());
		assertEquals("[/GRN]", to.getSuffix());
		assertEquals("GRN", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
